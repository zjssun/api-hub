package com.main.controller;

import com.main.entity.po.*;
import com.main.entity.query.*;
import com.main.entity.vo.ResponseVO;
import com.main.service.*;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/getMatch")
public class EptController extends ABaseController{
    @Resource
    DonkService donkService;

    @Resource
    EligeService eligeService;

    @Resource
    ImService imService;

    @Resource
    JlService jlService;

    @Resource
    M0nesyService m0nesyService;

    @Resource
    NikoService nikoService;

    @Resource
    RopzService ropzService;

    @Resource
    S1mpleService s1mpleService;

    @Resource
    TwistzzService twistzzService;

    @Resource
    UnknownService unknownService;

    @Resource
    W0nderfulService w0nderfulService;

    @Resource
    ZywooService zywooService;

    @Resource
    KyousukeService kyousukeService;

    private static final Logger logger = LoggerFactory.getLogger(EptController.class);
    @GetMapping("/{playerName}")
    public ResponseVO getPlayerInfo(@PathVariable String playerName) {
        if(playerName != null){
            switch (playerName) {
                case "donk" -> {
                    DonkQuery donkQuery = new DonkQuery();
                    donkQuery.setOrderBy("timestamp desc");
                    List<Donk> donkList = donkService.findListByParam(donkQuery);
                    return getSuccessResponseVO(donkList);
                }
                case "m0nesy" -> {
                    M0nesyQuery m0nesy = new M0nesyQuery();
                    m0nesy.setOrderBy("timestamp desc");
                    List<M0nesy> m0nesyList = m0nesyService.findListByParam(m0nesy);
                    return getSuccessResponseVO(m0nesyList);
                }
                case "s1mple" -> {
                    S1mpleQuery s1mple = new S1mpleQuery();
                    s1mple.setOrderBy("timestamp desc");
                    List<S1mple> s1mpleList = s1mpleService.findListByParam(s1mple);
                    return getSuccessResponseVO(s1mpleList);
                }
                case "niko" -> {
                    NikoQuery niko = new NikoQuery();
                    niko.setOrderBy("timestamp desc");
                    List<Niko> nikoList = nikoService.findListByParam(niko);
                    return getSuccessResponseVO(nikoList);
                }
                case "twistzz" -> {
                    TwistzzQuery twistzz = new TwistzzQuery();
                    twistzz.setOrderBy("timestamp desc");
                    List<Twistzz> twistzzList = twistzzService.findListByParam(twistzz);
                    return getSuccessResponseVO(twistzzList);
                }
                case "elige" -> {
                    EligeQuery elige = new EligeQuery();
                    elige.setOrderBy("timestamp desc");
                    List<Elige> eligeList = eligeService.findListByParam(elige);
                    return getSuccessResponseVO(eligeList);
                }
                case "ropz" -> {
                    RopzQuery ropz = new RopzQuery();
                    ropz.setOrderBy("timestamp desc");
                    List<Ropz> ropzList = ropzService.findListByParam(ropz);
                    return getSuccessResponseVO(ropzList);
                }
                case "jl" -> {
                    JlQuery jl = new JlQuery();
                    jl.setOrderBy("timestamp desc");
                    List<Jl> jlList = jlService.findListByParam(jl);
                    return getSuccessResponseVO(jlList);
                }
                case "zywoo" -> {
                    ZywooQuery zywoo = new ZywooQuery();
                    zywoo.setOrderBy("timestamp desc");
                    List<Zywoo> zywooList = zywooService.findListByParam(zywoo);
                    return getSuccessResponseVO(zywooList);
                }
                case "w0nderful" -> {
                    W0nderfulQuery w0nderful = new W0nderfulQuery();
                    w0nderful.setOrderBy("timestamp desc");
                    List<W0nderful> w0nderfulList = w0nderfulService.findListByParam(w0nderful);
                    return getSuccessResponseVO(w0nderfulList);
                }
                case "im" -> {
                    ImQuery im = new ImQuery();
                    im.setOrderBy("timestamp desc");
                    List<Im> imList = imService.findListByParam(im);
                    return getSuccessResponseVO(imList);
                }
                case "kyousuke"->{
                    KyousukeQuery kyousukeQuery = new KyousukeQuery();
                    kyousukeQuery.setOrderBy("timestamp desc");
                    List<Kyousuke> kyousukeList = kyousukeService.findListByParam(kyousukeQuery);
                    return getSuccessResponseVO(kyousukeList);
                }
            }
        }else {
            return getServerErrorResponseVO("没有该选手");
        }
        return getServerErrorResponseVO(null);
    }
}
