package com.monetware.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.monetware.service.collect.CollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Create by xuantang
 * @date on 11/5/17
 */

@RequestMapping("/collect")
@RestController
public class CollectController {

    @Autowired
    CollectService collectService;

    private StringBuilder res = new StringBuilder();
    private int success = 0;

    @RequestMapping(value = "/crawler", method = RequestMethod.POST)
    public String crawl(@RequestParam("data") String data) {
        /**
         * {
            "creep_pattern": "",
            "ajax": {
                "open": true,
                "ajax_pattern": "",
                "button_xpath": ""
                },
            "attribute_xpath": "",
            "attribute_xpath2": "",
            "attribute_name": "",
            "extract_way": "",
            "url_path": ""
           }
         */
        success = 0;
        res=new StringBuilder();
        JSONObject jsonObject = JSON.parseObject(data);

        // System.out.println(jsonObject.toJSONString());
        final String attribute_name = jsonObject.getString("attribute_name");
        String xpath1 = jsonObject.getString("attribute_xpath");
        String xpath2 = jsonObject.getString("attribute_xpath2");

        String url_path = jsonObject.getString("url_path");
        String extract_way = jsonObject.getString("extract_way");
        String crawlType =  jsonObject.getString("creep_pattern");
        com.alibaba.fastjson.JSONObject ajax = jsonObject.getJSONObject("ajax");

        boolean open = ajax.getBoolean("open");
        String ajax_pattern = ajax.getString("ajax_pattern");
        String button_xpath = ajax.getString("button_xpath");

        System.out.println(jsonObject.toJSONString());
        CollectService.OnCrawlListener onCrawlListener = new
                CollectService.OnCrawlListener() {
                    @Override
                    public void onSuccess(List<String> result) {
                        for (String str : result) {
                            res.append(attribute_name).append("\t").append(str).append("\n");
                        }
                        success = 1;
                    }

                    @Override
                    public void onFail(String error) {
                        res.append(error);
                        success = 2;
                    }
                };
        int collectType = 0;
        // ack the type
        if (crawlType.equals("线索") && open && ajax_pattern.equals("翻页")) {
            collectType = CollectService.TYPE_CLUES_AJAX_FLIP;
        }
        else if (crawlType.equals("线索") && open && ajax_pattern.equals("点击")) {
            collectType = CollectService.TYPE_CLUES_AJAX_CLICK;
        }
        else if (crawlType.equals("线索") && !open ) {
            collectType = CollectService.TYPE_CLUES;
        }
        // start crawler
        List<String> urls = new ArrayList<>();
        urls.add(url_path);
        collectService.crawl(onCrawlListener, urls, collectType, extract_way, button_xpath, xpath1, xpath2);
        // start crawler

        while (success == 0) {

        }
        return res.toString();
    }

}
