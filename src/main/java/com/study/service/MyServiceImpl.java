package com.study.service;

import com.study.util.ProtoUtil;

public class MyServiceImpl implements MyService {

	public ProtoResponse.msgInfo Plus(ProtoRequest.msgInfo req) {
		return ProtoUtil.createReqonse(req.getNum1() + req.getNum2());
	}
}
