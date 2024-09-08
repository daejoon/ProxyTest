# ProxyTest

JPA Entity 저장시 Proxy 상태의 연관관계 Entity의 null 반환 오류

## 증상

City를 저장할때 연관관계에 있는 Code의 엔티티를 참조 시키면 저장시 실제 테이블에 필드값이 자연스럽게 저장된다. 하지만 지금 경우에는 저장시 null 이 저장되면서 예외가 발생

## 원인

* JPA는 엔티티의 Lazy 설정이 되어 있거나 1차 캐쉬(PersistenceContext)에 저장되어 있는 데이터를 다시 조회 하면 HibernateProxy 객체를 리턴하고 실제 DB에서 가져오지 않는다.

* 저장시에 연관관계 Primary 조인 관계라면 실제 참조 엔티트 값을 조회한다.

    ```java
    /** SimpleForeignKeyDescriptor.java **/
    ...
    if ( refersToPrimaryKey ) {
      final LazyInitializer lazyInitializer = HibernateProxy.extractLazyInitializer( targetObject );
      if ( lazyInitializer != null ) {
          return lazyInitializer.getIdentifier();
      }
    }
    ...
    ```
* 저장시 연관관계 Primary 조인이 아니라면 값을 그대로 조회한다.

    ```java
    /** ToOneAttributeMapping.java */
    ...
    private static Object extractAttributePathValue(Object domainValue, EntityMappingType entityType, String attributePath) {
        if ( ! attributePath.contains( "." ) ) {
            return entityType.findAttributeMapping( attributePath ).getValue( domainValue );
        }
        ...
    }
    ...
    ```

## 조치방법

* 방법1
    * 연관관계 Primary 조인이 아니라면 Hibernate.unproxy 메소드를 사용해서 실제 참조 값을 리턴하게 변경해야 한다.

* 방법2
    * 연관관계 선언할때 참조 엔티의의 PrimaryKey와 조인이 되게 설계하고 그렇지 않으면 Join을 하지 말고 실제 키 값만 필드에 직접 가지고 있게 설계한다.

## 결론

* 방법 2 추천
    * 개발자가 개발하면서 연관 관계를 정확히 알아야 하며 휴먼에러가 발생하기 쉽다. 차라리 그럴바에는 실제 필드 값을 가지고 있게 수정하는게 관리상 용이하며 unrpoxy를 해야 하는 코드도 없어서 흐름도
      자연스럽다.