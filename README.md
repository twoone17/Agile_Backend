# f3f-back
재테크(주식,코인,부동산,달러,금리,금) 전반적인 내용을 공유하고 서로 토론할 수 있는 커뮤니티를 만드는게 목적이고 프로젝트이기도 하지만 정식 출시를 목표로 하는중

- **유저 서비스 (User)**
    - 로그인(일반 로그인 , 구글 로그인 , 카카오톡 로그인) , 로그아웃
    - 회원가입 , 회원 탈퇴
    - 유저 간의 Follow , UnFollow
    - 커뮤니티의 특성상 유저데이터는 많은 정보를 요구할 필요가 없어보인다.
    - 안보고 싶은 유저 차단
    - 전문가 인증을 한 유저는 전문가 등급을 별도로 제공 (사원증, 자격증)
    
- **관리자 서비스 (Admin)**
    - 문제가 생긴 게시글은 삭제 가능
    - 문제가 있는 유저들은 사용정지 기능, N일간 이용 정지 , 게시글 작성 금지
    
- **게시글 서비스 (Post)**
    - 게시글 작성(본인 게시글은 마이페이지에서 확인, 수정 , 삭제 가능) , 게시글 삭제 , 게시글 수정 , 게시글 검색
    - 마음에 드는 글은 좋아요 가능(마이 페이지에서 좋아요를 누른 게시글 확인가능)
    - 카테고리별로 게시글 분리
    - 게시글 내부에는 태그(= 해시태그 ) 기능을 넣어 관심있는 태그만 검색 가능
    - 일자별 조회 , 태그별 조회 , 주식 종목별 검색 , 코인 종목별 검색
    - 게시글 공유
- **게시글 태그 서비스 (tag)**    

- **데이터 관리 (Data)**
    - 주식 정보는 공공데이터를 활용해서 정보를 제공 할 예정
    - 주식 종가 데이터를 가져와서 종가 데이터만 활용
    - 각 지수별 변동 사항을 제공
    - 코인은 일정한 주기를 가지고 가져와서 제공
    - 각종 연설 일정을 알려주기엔 리소스가 너무 많이 들고 각 나라별 기관에서 하는 발표만 일정 공유
    - 금리에 대해서는 은행 데이터가 필요하고 적금이나 대출 등 은행데이터 수집은 조금 더 조사가 필요해 보임/

## 🎮 GIT FLOW

   * 개인 이름 브랜치를 만들어서 각자 개발사항을 추가 
   * master branch 에 올리기 전에 코드 리뷰하고 특이사항 없을 경우 PR Merge

## 🎮 GIT COMMIT MESSAGE CONVENTION
   * [ADD] , [REFACTOR] , [DELETE] , [HOTFIX]
   * 커밋 메세지는 모두가 보고 백트래킹이 되어야 하기 때문에 문장 자체는 한국어로 구성 
   * EX) [ADD] 유저 로그인 토근화 기능 추가

## 🎮 TEST CODE CONVENTION
- **테스트 코드 형식 (JUNIT5)**
````
  @DisplayName(value = "테스트 예제")
  @Test
  void test(){
        //given
        
        //when
        
        //then
  }
//
/
