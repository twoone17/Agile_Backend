package com.f3f.community.common.constants;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseMessage {
    /*
     * ResponseEntity
     * ResponseEntity는 HttpEntity를 상속받는 클래스로 결과 데이터 HTTP 상태코드를 직접 제어할 수 있는 응답에 독립체이다.
     * ResponseEntity는 사용자의 HttpRequest에 대한 응답 데이터를 포함하는 클래스이다.
     * 따라서 HttpStatus, HttpHeaders, HttpBody를 포함한다.
     * 모든 return 값이 객체가 아니기도하고 가끔은 상태코드,상태 메시지만으로 응답을 표현해야 할 때가 있다.
     * 이런 상태코드,상태메시지를 개발자가 제어를 할 수 있고 200과 같은 성공 응답이나 404,500,와 같은 에러를 상태코드나
     * 메시지로 대체가 가능하도록 도와주고 리턴값에 대한 내용이 세밀하게 관리될 수 있다.
     * 상세하게 응답을 구성하므로써 조금 더 해당 프로젝트에 맞는 응답을 구성하고 관리할 수 있게 된다.
     */

    public static final ResponseEntity OK = ResponseEntity.ok().build();

    public static final ResponseEntity<String> CREATE = new ResponseEntity<>(
            "SUCCESS",HttpStatus.CREATED);
}
