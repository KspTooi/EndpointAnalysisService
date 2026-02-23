package com.ksptool.bio.biz.relay.service;

import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.bio.biz.relay.model.request.GetRequestDetailsVo;
import com.ksptool.bio.biz.relay.model.request.GetRequestListDto;
import com.ksptool.bio.biz.relay.model.request.GetRequestListVo;
import com.ksptool.bio.biz.relay.model.request.RequestPo;
import com.ksptool.bio.biz.relay.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;


    /**
     * 获取请求列表
     *
     * @param dto 请求列表查询条件
     * @return 请求列表
     */
    public PageResult<GetRequestListVo> getRequestList(GetRequestListDto dto) {
        Page<GetRequestListVo> page = requestRepository.getRequestList(dto, dto.pageRequest());
        var ret = PageResult.success(page.getContent(), page.getTotalElements());
        return ret;
    }

    /**
     * 获取请求详情
     *
     * @param id 请求ID
     * @return 请求详情
     */
    public GetRequestDetailsVo getRequestDetails(Long id) {
        RequestPo item = requestRepository.findById(id).orElse(null);
        if (item == null) {
            return null;
        }
        GetRequestDetailsVo vo = new GetRequestDetailsVo();
        vo.setId(item.getId());
        vo.setRequestId(item.getRequestId());
        vo.setMethod(item.getMethod());
        vo.setUrl(item.getUrl());
        vo.setSource(item.getSource());
        vo.setStatusCode(item.getStatusCode());
        vo.setRequestBody(item.getRequestBody());
        vo.setResponseBody(item.getResponseBody());
        vo.setRequestHeaders(item.getRequestHeaders());
        vo.setResponseHeaders(item.getResponseHeaders());
        vo.setRequestBodyLength(item.getRequestBodyLength());
        vo.setResponseBodyLength(item.getResponseBodyLength());
        vo.setRequestBodyType(item.getRequestBodyType());
        vo.setResponseBodyType(item.getResponseBodyType());
        vo.setRedirectUrl(item.getRedirectUrl());
        vo.setStatus(item.getStatus());
        vo.setRequestTime(item.getRequestTime());
        vo.setResponseTime(item.getResponseTime());
        return vo;
    }


    /**
     * 提交请求
     *
     * @param request 请求
     */
    @Async
    public void commitRequest(RequestPo request) {

        requestRepository.save(request);

        //requestQueue.offer(request);
    }


}
