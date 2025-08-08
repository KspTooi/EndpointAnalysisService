package com.ksptooi.biz.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ksptooi.biz.model.request.GetRequestDetailsVo;
import com.ksptooi.biz.model.request.GetRequestListDto;
import com.ksptooi.biz.model.request.GetRequestListVo;
import com.ksptooi.biz.model.request.RequestPo;
import com.ksptooi.biz.repository.RequestRepository;
import com.ksptooi.commons.utils.web.PageableResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;

@Service
public class RequestService {

    //线程安全的阻塞队列
    private final BlockingQueue<RequestPo> requestQueue = new LinkedBlockingQueue<>();

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private Gson gson;

    /**
     * 获取请求列表
     * @param dto 请求列表查询条件
     * @return 请求列表
     */
    public PageableResult<GetRequestListVo> getRequestList(GetRequestListDto dto) {
        Page<RequestPo> pPos = requestRepository.getRequestList(dto, dto.pageRequest());
        List<GetRequestListVo> vos = new ArrayList<>();

        for (RequestPo item : pPos) {
            GetRequestListVo vo = new GetRequestListVo();
            vo.setId(item.getId());
            vo.setRequestId(item.getRequestId());
            vo.setMethod(item.getMethod());
            vo.setUrl(item.getUrl());
            vo.setSource(item.getSource());
            vo.setStatus(item.getStatus());
            vo.setRequestTime(item.getRequestTime());
            vo.setResponseTime(item.getResponseTime());
            vo.setStatusCode(item.getStatusCode());

            vo.setRequestBody("{}");
            vo.setResponseBody("{}");

            //处理请求体
            try{
                vo.setRequestBody(gson.fromJson(item.getRequestBody(), Map.class));
            }catch (Exception e){
            }
            try{
                vo.setResponseBody(gson.fromJson(item.getResponseBody(), Map.class));
            }catch (Exception e){
            }

            vos.add(vo);
        }
        return PageableResult.success(vos, pPos.getTotalElements());
    }

    /**
     * 获取请求详情
     * @param id 请求ID
     * @return 请求详情
     */
    public GetRequestDetailsVo getRequestDetails(Long id) {
        RequestPo item = requestRepository.findById(id).orElse(null);
        if(item == null){
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
     * @param request 请求
     */
    public void commitRequest(RequestPo request) {

        requestRepository.save(request);

        //requestQueue.offer(request);
    }

    

}
