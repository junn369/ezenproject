package ezenproject.service;

import java.util.List;

import ezenproject.dto.CouponDTO;
import ezenproject.dto.MemberDTO;

public interface CouponService {

	
	public void saveNewCouponProcess(CouponDTO dto);
	public void newCouponCodeProcess(CouponDTO dto);
	public List<CouponDTO> listProcess(String member_number);
	public void deleteCouponProcess(int num);
	public List<CouponDTO> myCouponlistProcess(String member_number);


}