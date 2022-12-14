package ezenproject.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ezenproject.dto.BoardDTO;
import ezenproject.dto.BookDTO;
import ezenproject.dto.CartDTO;
import ezenproject.dto.CouponDTO;
import ezenproject.dto.ListOrderDTO;
import ezenproject.dto.MemberDTO;
import ezenproject.dto.OrderDTO;
import ezenproject.dto.PageDTO;
import ezenproject.service.BoardService;
import ezenproject.service.BookService;
import ezenproject.service.CartService;
import ezenproject.service.CouponService;
import ezenproject.service.MemberService;
import ezenproject.service.OrderService;

// http://localhost:8090/

@CrossOrigin("*")
@Controller
public class MainController {

	@Autowired
	private BookService bservice;

	@Autowired
	private MemberService mservice;

	@Autowired
	private OrderService oservice;
	
	@Autowired
	private CouponService couponservice;
	
	@Autowired
	private CartService cservice;
	
	@Autowired
	private BoardService boardservice;
	
	private BookDTO bdto;
	private MemberDTO mdto;
	private OrderDTO odto;
	private PageDTO pdto;
	private CartDTO cdto;
	
	private int currentPage;

	public MainController() {
		// TODO Auto-generated constructor stub
	}

	@Value("${spring.servlet.multipart.location}")
	private String filepath;

//////////////////////??????????????? ??????????????? ?????? //////////////////	
	
//	?????? ????????? ??????
	// http://localhost:8090/
	@RequestMapping(value =  {"/","/index.do" } , method = RequestMethod.GET)
	public ModelAndView main(HttpServletRequest request, ModelAndView mav) {
		int totalRecord = bservice.countProcess();
		String viewname = (String) request.getAttribute("viewName");
		if (viewname == null) {
			viewname = "/index";
		}
		
		pdto = new PageDTO(1, totalRecord);
		List<BookDTO> alist = bservice.allBookListProcess(pdto);
		mav.addObject("alist", alist);
		
		mav.setViewName(viewname);

		return mav;
	}

	
//	////////////////////???????????? ??????????????? ?????? ////////////////////////////////////////////////
	
//	Form?????? ????????? ????????? ?????? ????????????(result = false)
	@RequestMapping(value = "/*/*Form.do", method = RequestMethod.GET)
	private ModelAndView form(@RequestParam(value = "result", required = false) String result,
			@RequestParam(value = "action", required = false) String action, HttpServletRequest request,
			HttpServletResponse response) {
		String viewName = (String) request.getAttribute("viewName");
		HttpSession session = request.getSession();
		session.setAttribute("action", action);

		ModelAndView mav = new ModelAndView();
		mav.addObject("result", result);
		mav.setViewName(viewName);
		return mav;
	}

	
	
//	/////////////////////////////// ??????????????? ?????????& ???????????? & ????????????/////////////////////////////////////////////////
//	????????? ?????? ??????
	@RequestMapping(value = "/member/login.do", method = RequestMethod.POST)
	public ModelAndView memberLoginMethod(@ModelAttribute("member") MemberDTO dto, RedirectAttributes rAttr,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		mdto = mservice.memberLogin(dto);
		if (mdto != null) {
			HttpSession session = request.getSession();
			session.setAttribute("member", mdto);
			session.setAttribute("isLogOn", true);

			String action = (String) session.getAttribute("action");
			session.removeAttribute("action");
			if (action != null) {
				mav.setViewName("redirect:" + action);
			} else {
				mav.setViewName("redirect:"+request.getHeader("Referer"));
			}
		} else {
			rAttr.addAttribute("result", "loginFailed");
			mav.setViewName("redirect:../common/loginalert.do");
		}

		return mav;
	}
	
//	????????? ??????
	@RequestMapping(value = "/*/*alert.do", method = RequestMethod.GET)
	private ModelAndView loginDODO(@RequestParam(value = "result", required = false) String result,
			@RequestParam(value = "action", required = false) String action, HttpServletRequest request,
			HttpServletResponse response) {
		String viewName = (String) request.getAttribute("viewName");
		HttpSession session = request.getSession();
		session.setAttribute("action", action);

		ModelAndView mav = new ModelAndView();
		mav.addObject("result", result);
		mav.setViewName(viewName);
		return mav;
	}
	

//	???????????? ?????? ??????
	@RequestMapping(value = "/member/logout.do", method = RequestMethod.GET)
	public String logout(HttpServletRequest request) throws Exception {
		HttpSession session = request.getSession();
		session.removeAttribute("member");
		session.removeAttribute("isLogin");

		return "redirect:/";
	}

//	?????????????????? ?????? 
	@RequestMapping(value = "/member_join", method = RequestMethod.POST)
	public ModelAndView addMember(@ModelAttribute("member") MemberDTO member, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");

		int result = 0;
		mservice.addMemberNumberProcess(member);
		result = mservice.addMemberProcess(member);
		ModelAndView mav = new ModelAndView("redirect:/");
		return mav;
	}

//	///////////////////////////////////???????????? ????????? & ???????????? & ????????????/////////////////////////////////////
	
//	///////////////////////////??????????????? ?????? ?????????////////////////////////////////////////

	//???????????? ??????
		@RequestMapping(value = "/mypage/memberdetail.do", method = RequestMethod.GET)
		public ModelAndView memberInformationMethod(ModelAndView mav, int num)
				throws Exception {
			mdto = mservice.selectOneProcess(num);
			mav.addObject("memberInfo", mdto);
			mav.setViewName("/mypage/memberdetail");
//			System.out.println(mdto);
			return mav;
		}
	
	
		//????????????
		@RequestMapping(value = "/mypage/memberdelete.do", method = RequestMethod.POST)
		public String memberLeaveMethod(int num, HttpServletRequest request)	throws Exception {
			mservice.statusChangeOffProcess(num);
			logout(request);
			return "redirect:/";
		}
		
		
		//???????????? ??????
		@RequestMapping(value = "/mypage/update.do", method = RequestMethod.POST)
		public String updateMethod(MemberDTO mdto) throws Exception {
			mservice.updateInformation(mdto);
			return "redirect:/mypage/mypageForm.do";
		}
		
		
		//?????? ??????
		@RequestMapping(value = "/mypage/myorderlist.do", method = RequestMethod.GET)
		public ModelAndView orderListMethod(ModelAndView mav, String member_number) {	 
			 
			 List<OrderDTO> aList = oservice.myOrderListProcess(member_number);

			 
			// ??????????????? ???????????? ?????? 
			List<Object> ordernumbers = new ArrayList<Object>();
			
			for(int i=0; i<aList.size(); i++) {
				ordernumbers.add(aList.get(i).getOrder_number());
			};
			
			TreeSet<Object> checkordernumbers = new TreeSet<Object>(ordernumbers);
			List<Object> realordernumbers = new ArrayList<Object>(checkordernumbers);
			
			
			 mav.addObject("orderNumbers", realordernumbers);
			 mav.addObject("aList", aList); 
			mav.setViewName("/mypage/myorderlist");
			return mav;
		} 
		
		
		//?????? ????????????
		@RequestMapping(value = "/mypage/myorderdetail.do", method = RequestMethod.GET)
		public ModelAndView orderInformationMethod(ModelAndView mav, int num)
				throws Exception {
			odto = oservice.orderInformationProcess(num);
			mav.addObject("orderInfo", odto);
			mav.setViewName("/mypage/myorderdetail");
			//System.out.println(obdto.getBdto().getBook_content());
			return mav;
		}
		
		
		
		//?????? ??????
		@RequestMapping(value="/mypage/myorderstatus.do", method = RequestMethod.GET)
		public ModelAndView orderStatusMethod(int onum, ModelAndView mav) throws Exception {
			odto = oservice.orderStatusProcess(onum);
			mav.addObject("orderstatus", odto);
			mav.setViewName("/mypage/myorderstatus");
			
			return mav;
		}
		
		
		//?????? ??????(???????????? ????????? ?????? ??????)
		@RequestMapping(value = "/mypage/myorderupdate.do", method = RequestMethod.POST)
		public String orderupdateMethod(OrderDTO dto, String member_number) throws Exception {
			
			oservice.updateOrderProcess(dto);

			return "redirect:/mypage/myorderlist.do?member_number="+member_number;
		}
		
		
		//????????? ??????
				@RequestMapping(value = "/mypage/mycoupon.do", method = RequestMethod.GET)
				public ModelAndView myCouponlist(ModelAndView mav, String member_number) {	 
					 
					 List<CouponDTO> aList = couponservice.listProcess(member_number);
					
					 mav.addObject("aList", aList); 
					// System.out.println(aList);
					mav.setViewName("/mypage/mycoupon");
					return mav;
				} 
		
				
				//?????? ?????? ????????? ???????????????
				@RequestMapping(value = "/mypage/myboardlist.do", method = RequestMethod.GET)
				public ModelAndView myboardlistMethod(PageDTO pv, ModelAndView mav,String member_id,int board_type) {
					Map<String,Object>map = new HashMap<String,Object>();
					map.put("board_type", board_type);
					map.put("member_id", member_id);
					int totalRecord = boardservice.myCountProcess(map);
					if (totalRecord >= 1) {
						if(pv.getCurrentPage()==0)
							currentPage = 1;
						else
							currentPage = pv.getCurrentPage();	
					pdto = new PageDTO(currentPage, totalRecord);
					List<BoardDTO> aList = boardservice.myBoardListProcess(pdto,board_type,member_id);
					mav.addObject("aList", aList);
					mav.addObject("pv", pdto);
					}
					mav.setViewName("mypage/myboardlist");
					return mav;
				}
				
				//?????? ?????? ????????? ????????????
				@RequestMapping("/mypage/boardview.do")
				public ModelAndView myViewMethod(int currentPage,BoardDTO dto, ModelAndView mav) throws Exception {
					mav.addObject("dto",boardservice.contentProcess(dto));
					mav.addObject("currentPage", currentPage);
					mav.setViewName("mypage/boardview");
					return mav;
				}
				
				// ?????? ??????????????? ??????
				@RequestMapping(value = "/mypage/boardupdate.do", method = RequestMethod.GET)
				public ModelAndView myUpdateMethod(HttpServletRequest request, BoardDTO dto, int currentPage, ModelAndView mav) {
					String viewName = (String) request.getAttribute("viewName");
					mav.addObject("dto", boardservice.contentProcess(dto));
					mav.addObject("currentPage", currentPage);
					mav.setViewName(viewName);
					return mav;
				}
				
				//?????? ?????? ????????? ??????
				@RequestMapping(value = "/mypage/boardupdate.do", method = RequestMethod.POST)
				public String myUpdateProMethod(BoardDTO dto, int currentPage, HttpServletRequest request) {
					MultipartFile file = dto.getFilename();
					if(!file.isEmpty()) {
						UUID random = saveCopyFile(file, request);
						dto.setUpload(random + "_" + file.getOriginalFilename());
					}
					
					boardservice.updateProcess(dto, urlPath(request));
					return "redirect:/mypage/myboardlist.do?currentPage=" + currentPage+"&&board_type="+dto.getBoard_type()+"&&member_id="+dto.getMember_id();
				}
				
				//?????? ?????? ????????? ??????
				@RequestMapping(value = "/mypage/boarddelete.do", method = RequestMethod.GET)
				public String myDeleteMethod(BoardDTO dto, int currentPage, HttpServletRequest request) {
					boardservice.deleteProcess(dto, urlPath(request));
					return "redirect:/mypage/myboardlist.do?currentPage=" + currentPage+"&&board_type="+dto.getBoard_type()+"&&member_id="+dto.getMember_id();
				}
		
	
//	///////////////////////////???????????? ???????????????//////////////////////////////////////////////
	
	
	
	
	
//////////////////////////////??????????????? ?????? ?????????/////////////////////////////////////////////////////	
	
//	http://localhost:8090/book/allBooklist.do
//	?????? ?????? ?????? ?????????(?????? ???????????????)
	@RequestMapping(value = "/book/*Booklist.do")
	public ModelAndView listAllBookMethod(HttpServletRequest request, PageDTO pv, ModelAndView mav) {
		int totalRecord = bservice.countProcess();
		String viewName = (String) request.getAttribute("viewName");
		if (totalRecord != 0) {
			if (pv.getCurrentPage() == 0) {
				currentPage = 1;
			} else {
				currentPage = pv.getCurrentPage();
			}
			pdto = new PageDTO(currentPage, totalRecord);
			List<BookDTO> alist = bservice.allBookListProcess(pdto);
			List<BookDTO> newList = bservice.newBookListProcess(pdto);
			mav.addObject("newList", newList);
			mav.addObject("alist", alist);
			mav.addObject("pv", pdto);

		}
		mav.setViewName(viewName);

		return mav;
	}

//	??????????????? ??? ?????????
	@RequestMapping(value = "/book/*Categorylist.do", method = RequestMethod.GET)
	public ModelAndView listCategoryBookMethod(HttpServletRequest request, PageDTO pv, ModelAndView mav,
			int book_category, String categoryName) {
		int totalRecord = bservice.countCategoryProcess(book_category);
		String viewName = (String) request.getAttribute("viewName");
		if (totalRecord != 0) {
			if (pv.getCurrentPage() == 0) {
				currentPage = 1;
			} else {
				currentPage = pv.getCurrentPage();
			}
			pdto = new PageDTO(currentPage, totalRecord);
			List<BookDTO> alist = bservice.categoryBookListProcess(pdto, book_category);
			mav.addObject("book_category", book_category);
			mav.addObject("categoryName", categoryName);
			mav.addObject("alist", alist);
			mav.addObject("pv", pdto);

		}
		mav.setViewName(viewName);

		return mav;
	}

//	????????? ??? ?????????
	@RequestMapping(value = "/book/searchlist.do", method = RequestMethod.GET)
	public ModelAndView listSearchMethod(HttpServletRequest request, PageDTO pv, ModelAndView mav, String searchWord) {
		int totalRecord = bservice.countSearchProcess(searchWord);
		String viewName = (String) request.getAttribute("viewName");
		if (totalRecord != 0) {
			if (pv.getCurrentPage() == 0) {
				currentPage = 1;
			} else {
				currentPage = pv.getCurrentPage();
			}
			pdto = new PageDTO(currentPage, totalRecord);

			pdto.setSearchWord(searchWord);

			List<BookDTO> alist = bservice.searchListProcess(pdto);

			mav.addObject("alist", alist);
			mav.addObject("pv", pdto);

		}
		mav.setViewName(viewName);

		return mav;
	}

	
//	///////////////////////////???????????? ?????? ?????????////////////////////////////////////////////
	
///////////////////////////??????????????? ?????? ?????? ?????????//////////////////////////////////////	
	
//	?????? ?????? ????????? ????????????
	@RequestMapping(value = "/book/book_detail.do")
	public ModelAndView viewMethod(HttpServletRequest request, int currentPage, int num, ModelAndView mav) {
		String viewName = (String)request.getAttribute("viewName");
		List<BookDTO> alist = bservice.listProcess();
		mav.addObject("alist", alist);
		
		try {
			if(bservice.contentProcess(num).getNum()==num) {
					mav.addObject("dto", bservice.contentProcess(num));
					
					mav.addObject("currentPage", currentPage);
					mav.setViewName(viewName);
		}
			
		}catch (Exception e) {
			viewName = "/erroralert";
			mav.setViewName(viewName);
		}
		
		return mav;
		
	}
	
	
//	////////////////////////////???????????? ?????? ?????? ?????????./////////////////////////////////////////
	
	
	
//////////////////??????????????? ??????????????? ////////////////////////////////////////////////////////////



//	?????? ????????? ????????????
	@RequestMapping(value = "/order/orderDetail.do")
	public ModelAndView viewMethod(HttpServletRequest request, int num, ModelAndView mav,
			String member_number, int book_qty) {
 bdto= bservice.contentProcess(num);
List<CouponDTO> couponlist = couponservice.listProcess(member_number);
		String viewName = (String) request.getAttribute("viewName");

		mav.addObject("book_qty", book_qty);
		mav.addObject("bdto", bdto);
		mav.addObject("couponlist", couponlist);
		mav.setViewName(viewName);
		
		return mav;

	}


	
	
//	???????????? ??????
	@RequestMapping(value = "/order/ordersave.do", method = RequestMethod.POST)
	public String newOrderMethod(OrderDTO dto, HttpServletRequest request, String coupon_number) {
		oservice.newOrderNumberProcess(dto);
		oservice.newOrderSaveProcess(dto);
		couponservice.usedCouponProcess(coupon_number);
		
		return "redirect:/";
	}
	
	
	
	/* ???????????? ?????? ????????? */
	@RequestMapping(value ="/order/orderCartDetail/{member_number}" , method = RequestMethod.GET)
	public String cartOrderGET(@PathVariable("member_number") String member_number, Model model) {
	model.addAttribute("clist", cservice.getCartProcess(member_number));
	model.addAttribute("couponlist", couponservice.listProcess(member_number));

	return "/order/orderCartDetail";
	}	
	
	
//	???????????? ????????? ???????????? ??????
	@RequestMapping(value = "/order/cartordersave.do", method = RequestMethod.POST)
	public String newCartOrderMethod(ListOrderDTO listorder, HttpServletRequest request, String coupon_number,
			int order_cost, String member_number, String order_phone, String order_name, String order_address) {
		
		OrderDTO standarddto=listorder.getOrderDTO().get(0);
				oservice.newOrderNumberProcess(standarddto);
				String standardcode = standarddto.getOrder_number();
				
				
		for(int i=0; i< listorder.getOrderDTO().size(); i++ ) {
			OrderDTO alist= listorder.getOrderDTO().get(i);
			alist.setOrder_number(standardcode);
			alist.setOrder_cost(order_cost);
			alist.setMember_number(member_number);
			alist.setOrder_phone(order_phone);
			alist.setOrder_name(order_name);
			alist.setOrder_address(order_address);
			
			oservice.newOrderSaveProcess(alist);
		}
		

		cservice.alldeleteCartProcess(member_number);
		couponservice.usedCouponProcess(coupon_number);
		
		return "redirect:/";
	}
	
	
	
	
//	////////////////////////////???????????? ?????? ?????????///////////////////////////////////////
	
	
	
////////////////////////////////???????????? ????????? ??????//////////////////////////////////////

/* ???????????? ?????? */
/**
* 0: ?????? ??????
* 1: ?????? ??????
* 2: ????????? ????????? ??????
*/


@ResponseBody
@RequestMapping(value = "/cart/list/add")
public String addCartPOST(CartDTO dto, HttpServletRequest request) {

int result = cservice.addCartProcess(dto);
return result + "";	
}






/* ???????????? ????????? ?????? */	

@RequestMapping(value ="/cart/list/{member_number}" , method = RequestMethod.GET)
public String cartPageGET(@PathVariable("member_number") String member_number, Model model) {
model.addAttribute("clist", cservice.getCartProcess(member_number));

return "/cart/list";
}	
/* ???????????? ?????? ?????? */
@RequestMapping(value = "/cart/list/update" , method = RequestMethod.POST)
public String updateCartPOST(CartDTO dto) {
cservice.modifyCountProcess(dto);
return "redirect:/cart/list/" + dto.getMember_number();

}	
/* ???????????? ?????? */
@RequestMapping(value = "/cart/list/delete" , method = RequestMethod.POST)
public String deleteCartDELETE(CartDTO dto) {
cservice.deleteCartProcess(dto.getNum());
return "redirect:/cart/list/" + dto.getMember_number();
}		



/* ??????????????? ???????????? ?????? ?????? */
@ResponseBody
@RequestMapping(value = "/order/orderCartDetail/update" , method = RequestMethod.PUT)
public void updateOrderCartPOST(CartDTO dto) {
cservice.modifyCountProcess(dto);
}	
/* ??????????????? ???????????? ?????? */
@ResponseBody
@RequestMapping(value = "/order/orderCartDetail/delete" , method = RequestMethod.DELETE)
public void deleteOrderCartDELETE(CartDTO dto, int num) {
cservice.deleteCartProcess(num);
}	



////////////////////???????????? ????????? ???////////////////////////////////



//////////////////////////////////??????????????? ????????? ??????????????????////////////////////////////////////////////////

//??????????????? ?????????
@RequestMapping("/board/boardForm.do")
public ModelAndView listMethod(PageDTO pv, ModelAndView mav) {
int totalRecord = boardservice.countProcess(0);
if (totalRecord >= 1) {
if(pv.getCurrentPage()==0)
currentPage = 1;
else
currentPage = pv.getCurrentPage();	
pdto = new PageDTO(currentPage, totalRecord);
List<BoardDTO> aList = boardservice.listProcess(pdto);
mav.addObject("aList", aList);
mav.addObject("pv", pdto);
}
mav.setViewName("board/boardForm");
return mav;
}

//??????,??????????????? ?????????
@RequestMapping("/board/*board.do")
public ModelAndView otherListMethod(HttpServletRequest request,PageDTO pv, ModelAndView mav,int board_type) {
int totalRecord = boardservice.countProcess(board_type);
String viewName = (String) request.getAttribute("viewName");
if (totalRecord >= 1) {
if(pv.getCurrentPage()==0)
currentPage = 1;
else
currentPage = pv.getCurrentPage();	
pdto = new PageDTO(currentPage, totalRecord);
List<BoardDTO> aList = boardservice.otherBoardListProcess(pdto,board_type);
mav.addObject("aList", aList);
mav.addObject("pv", pdto);
}
mav.setViewName(viewName);
return mav;
}

//????????? ?????????
@RequestMapping(value = "/board/write.do", method = RequestMethod.GET)
public ModelAndView writeMethod(BoardDTO dto,PageDTO pv, ModelAndView mav,int board_type) throws Exception {
mav.addObject("board_type", board_type);
if(dto.getRef()!=0) { //???????????????
mav.addObject("currentPage", pv.getCurrentPage());
mav.addObject("dto",dto);
}
mav.setViewName("board/write");
return mav;
}

@RequestMapping(value = "/board/write.do", method = RequestMethod.POST)
public String writeProMethod(BoardDTO dto,PageDTO pv, HttpServletRequest request) {
MultipartFile file = dto.getFilename();
if(!file.isEmpty()) {
UUID random = saveCopyFile(file, request);
dto.setUpload(random + "_" + file.getOriginalFilename());
}
boardservice.insertProcess(dto);
String path ="";
if(dto.getBoard_type()==0) {
path="boardForm";
}else if(dto.getBoard_type()==1) {
path="reviewboard";
}else {
path="qnaboard";
}

//???????????????
if(dto.getRef()!=0) {
return "redirect:/board/"+path+".do?currentPage=" + pv.getCurrentPage()+"&&board_type=" + dto.getBoard_type();
}else {
return "redirect:/board/"+path+".do?board_type=" + dto.getBoard_type();
}


}

private String urlPath(HttpServletRequest request) {
String root = request.getSession().getServletContext().getRealPath("/");
//C:\smart_study\spring_workspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\spring08_board\temp
//System.out.println("root:" + root);
String saveDirectory = root + "temp" + File.separator;
return saveDirectory;
}

//????????? ????????????
private UUID saveCopyFile(MultipartFile file, HttpServletRequest request) {
String fileName = file.getOriginalFilename();

UUID random = UUID.randomUUID();

File fe = new File(urlPath(request));
if(!fe.exists()) {
fe.mkdir();
}

File ff = new File(urlPath(request), random+"_"+fileName);

try {
FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(ff));

} catch (IOException e) {
e.printStackTrace();
}
return random;
}

//????????????
@RequestMapping("/board/view.do")
public ModelAndView viewMethod(int currentPage,BoardDTO dto, ModelAndView mav) throws Exception {
mav.addObject("dto",boardservice.contentProcess(dto));
mav.addObject("currentPage", currentPage);
mav.setViewName("board/view");
return mav;
}

//???????????? ????????????
@RequestMapping("/*/contentdownload.do")
public ModelAndView downMethod(BoardDTO dto, ModelAndView mav) {
mav.addObject("dto", dto);
mav.setViewName("download");
return mav;
}

//????????? ??????
@RequestMapping(value = "/board/update.do", method = RequestMethod.GET)
public ModelAndView updateMethod(HttpServletRequest request, BoardDTO dto, int currentPage, ModelAndView mav) {
String viewName = (String) request.getAttribute("viewName");
mav.addObject("dto", boardservice.contentProcess(dto));
mav.addObject("currentPage", currentPage);
mav.setViewName(viewName);
return mav;
}

//????????? ??????
@RequestMapping(value = "/board/update.do", method = RequestMethod.POST)
public String updateProMethod(BoardDTO dto, int currentPage, HttpServletRequest request) {
MultipartFile file = dto.getFilename();
if(!file.isEmpty()) {
UUID random = saveCopyFile(file, request);
dto.setUpload(random + "_" + file.getOriginalFilename());
}

boardservice.updateProcess(dto, urlPath(request));
String path ="";
if(dto.getBoard_type()==0) {
path="boardForm";
}else if(dto.getBoard_type()==1) {
path="reviewboard";
}else {
path="qnaboard";
}
return "redirect:/board/"+ path +".do?currentPage=" + currentPage+"&&board_type="+dto.getBoard_type();
}

//????????? ??????
@RequestMapping(value = "/board/delete.do", method = RequestMethod.GET)
public String deleteMethod(BoardDTO dto, int currentPage, HttpServletRequest request) {
boardservice.deleteProcess(dto, urlPath(request));
String path ="";
if(dto.getBoard_type()==0) {
path="boardForm";
}else if(dto.getBoard_type()==1) {
path="reviewboard";
}else {
path="qnaboard";
}
return "redirect:/board/"+ path +".do?currentPage=" + currentPage+"&&board_type="+dto.getBoard_type();
}


///////////////////////////////???????????? ????????? ??????????????????/////////////////////////////
	

////////////////////////////////////////////////////???????????? ????????? ????????? ??????????????????.////////////////////

//	????????? ????????? ?????? ????????? ??????
	@ResponseBody
	@RequestMapping(value = "/books/list")
	public Map<String, Object> listBookMethod(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();

		List<BookDTO> alist = bservice.listProcess();

//		System.out.println(alist.get(0).getBook_title());
		map.put("alist", alist);

		return map;
	}

//	?????? ?????? ??????
	@ResponseBody
	@RequestMapping(value = "/books/statuschange/{num}", method = RequestMethod.PUT)
	public void statusBookChangeMethod(@PathVariable("num") int num) {

		bservice.statusCheckProcess(num);

	}

//	?????? ?????? ??????
	@ResponseBody
	@RequestMapping(value = "/books/stockchange/{num}", method = RequestMethod.PUT)
	public void stockBookChangeMethod(@PathVariable("num") int num) {

		bservice.stockCheckProcess(num);

	}

//	????????? ?????? ??????
	@ResponseBody
	@RequestMapping(value = "/books/newbooksave", method = RequestMethod.POST)
	public void newBookMethod(BookDTO dto) {
		MultipartFile file = dto.getFilename();
		if (file != null && !file.isEmpty()) {
			UUID ran = saveCopyFile(file);
			dto.setBook_img(ran + "_" + file.getOriginalFilename());
		}

		bservice.newBookIDProcess(dto);
		bservice.newBookProcess(dto);

	}

//	??? ?????? ??????
	private UUID saveCopyFile(MultipartFile file) {
		String filename = file.getOriginalFilename();

		// ?????? ???????????? ???????????? ?????? ?????? ??????
		UUID ran = UUID.randomUUID();

		File fe = new File(filepath); // ????????? ????????? ????????????
		if (!fe.exists()) {
			fe.mkdirs();
		}

		File ff = new File(filepath, ran + "_" + filename);

		try {
			FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(ff));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//				System.out.println("?????? ??????:  "+filepath);
		return ran;

	}

//	?????? ?????? ????????? ?????? ??????
	@ResponseBody
	@RequestMapping(value = "/books/deletedata/{num}", method = RequestMethod.DELETE)
	public void deleteBookDataMethod(@PathVariable("num") int num) {
		bservice.deleteDataProcess(num, filepath);
	}

//	?????? ?????? ?????? ??????
	@ResponseBody
	@RequestMapping(value = "/books/updatebook", method = RequestMethod.PUT)
	public void updateBookMethod(BookDTO dto) {
		MultipartFile file = dto.getFilename();
		System.out.println(dto.getBook_title());
		if (file != null && !file.isEmpty()) {
			UUID ran = saveCopyFile(file);
			dto.setBook_img(ran + "_" + file.getOriginalFilename());
		}

		bservice.updateBookProcess(dto, filepath);

	}

	@ResponseBody
	@RequestMapping(value = "/books/selectone/{num}", method = RequestMethod.GET)
	public BookDTO selectOneBookMethod(@PathVariable("num") int num) {

		return bservice.selectOneProcess(num);
	}

//	????????? ????????? ?????? ????????? ??????
	@ResponseBody
	@RequestMapping(value = "/members/list")
	public Map<String, Object> listMemberMethod(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();

		List<MemberDTO> alist = mservice.listProcess();

//		System.out.println(alist.get(0).getMember_id());
		map.put("alist", alist);

		return map;
	}

//	?????? ?????? ??????
	@ResponseBody
	@RequestMapping(value = "/members/statuschange/{num}", method = RequestMethod.PUT)
	public void statusMemberChangeMethod(@PathVariable("num") int num) {

		mservice.statusCheckProcess(num);

	}

//	?????? ?????? ??????
	@ResponseBody
	@RequestMapping(value = "/members/typechange/{num}", method = RequestMethod.PUT)
	public void typeMemberChangeMethod(@PathVariable("num") int num) {

		mservice.typeCheckProcess(num);

	}

//	?????? ????????? ?????? ??????
	@ResponseBody
	@RequestMapping(value = "/members/deletedata/{num}", method = RequestMethod.DELETE)
	public void deleteMemberDataMethod(@PathVariable("num") int num) {
		mservice.deleteDataProcess(num);
	}

//	????????? ????????? ?????? ????????? ??????
	@ResponseBody
	@RequestMapping(value = "/orders/list")
	public Map<String, Object> listOrderMethod(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();

		List<OrderDTO> alist = oservice.listProcess();

		map.put("alist", alist);

		return map;
	}

//	?????? ?????? ??????
	@ResponseBody
	@RequestMapping(value = "/orders/statuschange", method = RequestMethod.PUT)
	public void changeOrderStatusMethod(OrderDTO dto) {

		oservice.statusChangeProcess(dto);

	}

//	?????? ????????? ?????? ??????
	@ResponseBody
	@RequestMapping(value = "/orders/deletedata/{num}", method = RequestMethod.DELETE)
	public void deleteOrderDataMethod(@PathVariable("num") int num) {
		oservice.deleteDataProcess(num);
	}
	
	
//	???????????? ????????? ??????
	@ResponseBody
	@RequestMapping(value = "/members/newcouponsave", method = RequestMethod.POST)
	public void newCouponMethod(CouponDTO dto) {
		

		couponservice.newCouponCodeProcess(dto);
		couponservice.saveNewCouponProcess(dto);
	}
	
//	????????? ????????? ?????? ?????? ????????? ?????? ??????
	@ResponseBody
	@RequestMapping(value = "/members/couponlist/{member_number}")
	public Map<String, Object> listCouponMethod(HttpServletRequest request, 
			@PathVariable("member_number") String member_number) {
		Map<String, Object> map = new HashMap<String, Object>();

		List<CouponDTO> alist = couponservice.listProcess(member_number);


		map.put("alist", alist);

		return map;
	}

	
	
//	????????? ??????
	@ResponseBody
	@RequestMapping(value = "/members/deletecoupon/{num}", method = RequestMethod.DELETE)
	public void deleteCouponMethod(@PathVariable("num") int num) {
		couponservice.deleteCouponProcess(num);
	}
	
	
	

	// ????????? ????????? ????????? ????????? ??????
		@ResponseBody
		@RequestMapping(value = "/boards/list/{board_type}")
		public Map<String, Object> listBoardMethod(@PathVariable("board_type") int board_type) {
			Map<String, Object> map = new HashMap<String, Object>();
			List<BoardDTO> alist = boardservice.listAllProcess(board_type);
			map.put("alist", alist);
			return map;
		}

	// ????????? ????????? ????????? ???????????? ????????????
		@ResponseBody
		@RequestMapping("/boards/contentdownload/{board_type}/{num}")
		public void downloadMethod(@PathVariable("board_type") int board_type, @PathVariable("num") int num, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, IOException {
			BoardDTO dto = new BoardDTO();
			dto.setBoard_type(board_type);
			dto.setNum(num);
			String root = request.getSession().getServletContext().getRealPath("/");
			String saveDirectory = root + "temp" + File.separator;
			
			String upload = boardservice.fileSelectprocess(dto);
			System.out.println(upload);
			String fileName = upload.substring(upload.indexOf("_")+1);
			
			//???????????? ???????????? ????????? ????????? ??????.
			String str = URLEncoder.encode(fileName, "UTF-8");
			
			//?????????????????? ????????? ?????? ???, +??? ????????? ????????? ???????????? ????????????
			str = str.replaceAll("\\+", "%20");
			
			//????????? ??????
			response.setContentType("application/octet-stream");
			
			//?????????????????? ?????? ??? ???????????? ????????????
			response.setHeader("Content-Disposition", "attachment;filename="+str+";");
			
			//????????? ????????? ????????? ????????? ?????????????????? ????????? ??????.
			FileCopyUtils.copy(new FileInputStream(new File(saveDirectory, upload)), response.getOutputStream());
		}
		

//		???????????????????????? ????????? ??????
		@ResponseBody
		@RequestMapping(value = "/boards/delete/{num}/{board_type}", method = RequestMethod.DELETE)
		public void delete(@PathVariable("num") int num, @PathVariable("board_type")int board_type, HttpServletRequest request) {
			BoardDTO dto = new BoardDTO();
			dto.setBoard_type(board_type);
			dto.setNum(num);
			boardservice.deleteProcess(dto, urlPath(request));
		}
		
//		????????? ??????
		@ResponseBody
		@RequestMapping(value = "/boards/reply/{re_step}/{re_level}", method = RequestMethod.POST)
		public void writeProMethod(@PathVariable("re_step") int re_step ,@PathVariable("re_level") int re_level , BoardDTO dto,HttpServletRequest request ) {
			dto.setRe_step(re_step);
			dto.setRe_level(re_level);
			dto.setMember_id("admin");
			dto.setMember_email("admin@ezenbook.com");
			System.out.println(dto.getRe_step());
			MultipartFile file = dto.getFilename();
			if(file != null && !file.isEmpty()) {
				UUID random = saveCopyFile(file, request);
				dto.setUpload(random + "_" + file.getOriginalFilename());
			}
			boardservice.insertProcess(dto);
		}

//////////////////////////////////////////////////////???????????? ????????? ????????? ??????????????????.///////////////	

}