 package org.sopt.app.common.fixtures;

 import org.sopt.app.domain.entity.User;

 public class UserFixture {

     public static Long myAppUserId = 100L;

     public static User createMyAppUser() {
         return User.builder()
                 .id(myAppUserId)
                 .build();
     }
 }
