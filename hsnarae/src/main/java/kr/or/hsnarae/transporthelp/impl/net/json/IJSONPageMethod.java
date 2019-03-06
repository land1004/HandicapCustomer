package kr.or.hsnarae.transporthelp.impl.net.json;

public interface IJSONPageMethod
{
	enum JSON_PAGE_METHOD
	{
		Intro,
		Check,
		Login,
		UserInfo,
		RegMember,
		SmsAuthSend,
		SmsAuthCheck,
		IdCheck,
		HandiTypeList,
		HandiDegreeList,
		UseTypeList,
		PoiSearch,
		ReverseGeocoding,
		ReqCall,
		ReservReqCall,
		ReqCallCancel,
		CarPos,
		CallInfo,
		NoticeList,
		NoticeDetail,
		MemberInfo,
		ChgMemberInfo,
		PwConfirm,
		CallHistory,
		FindRoadInfo,
		TempPassword,
		CallAvailableTime
	}
}
