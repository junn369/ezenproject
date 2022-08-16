<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>

  <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
      rel="stylesheet"
      integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3"
      crossorigin="anonymous"
    />
 <script
      defer
      src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"
      integrity="sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p"
      crossorigin="anonymous"
    ></script>
<link href="../assets/css/style.css" rel="stylesheet" />
<script type="text/javascript">
$(document).ready(function(){
	console.log('${orderNumbers}');
});

function orderstatus(status,path){
	
	if(status!=0){
	location.href='http://localhost:8090/'+path;
		
	}else{
		alert("주문취소된 결재건입니다.");
	}
}
</script>
<style >
body {width:100%; height:100%; margin:0; padding:0; overflow-y:scroll; position:relative;} 
html {width:100%; height:100%; margin:0; padding:0; overflow:hidden;}
 #ordertable{
    position:relative;
   	bottom:120px;
    left:300px;
    width:70%;
    
    }
</style>
</head>
<body >

     <!-- Header start -->

    <%@ include file = "../common/header.jsp"%>

    <!-- Header end -->
    
  <!-- mypage menu start -->
  <div class="container">
	<%@ include file = "mypagemenu.jsp"%>
  <!-- mypage menu end -->
    
  <!-- order detail start -->

  
<table id='ordertable' class="table table-hover border border-secondary" >
    <c:forEach items="${orderNumbers}" var="orderNumbers">
  
   <thead>
   <tr><td> 주문 번호 : <c:out value="${orderNumbers }"/></td></tr>
   
    <tr >
      
      <th scope="col">책 제목</th>
      <th scope="col">주문수량</th>
      <th scope="col">총 결제금액</th>
      <th scope="col">배송현황</th>
    </tr>
  </thead>
  <tbody >
 
  <c:forEach items="${aList}" var="dto">
 <c:if test="${dto.order_number == orderNumbers }">
  	<c:url var="path" value="mypage/myorderdetail.do">
		<c:param name="num" value="${dto.num}" />
		<c:param name="member_number" value="${dto.member_number}" />
	</c:url>
    <tr onclick="orderstatus('${dto.order_status}','${path}')">
	  <td>${dto.order_number}</td>
      <td>${dto.ezenbooks.book_title}</td>
     
      <td>${dto.book_qty}권</td>
      <td>${dto.order_cost}원</td>
      <c:set var="status" value="${dto.order_status}" scope="session"/>
      <c:choose>
      <c:when test="${status == 0}"  >
      <td style="color: red">주문취소</td>
      </c:when>
      <c:when test="${status == 1}">
      <td>주문접수</td>
      </c:when>
      <c:when test="${status == 2}">
      <td>상품준비</td>
      </c:when>
      <c:when test="${status == 3}">
      <td>배송준비</td>
      </c:when>
      <c:when test="${status == 4}">
      <td>배송중</td>
      </c:when>
      <c:when test="${status == 5}">
      <td>배송완료</td>
      </c:when>
      </c:choose>
    </tr>
    </c:if>
 </c:forEach>
</tbody>
  </c:forEach>
  
  
 
</table>
  </div>
  <!-- order detail end -->
  
    <!-- Footer Start -->
 <%@ include file = "../common/footer.jsp"%>
  <!-- Footer end -->
  
</body>
</html>