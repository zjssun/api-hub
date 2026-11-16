package com.main.controller;

import com.main.component.ProPlayerRegistry;
import com.main.entity.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pros")
public class FaceitProfileController extends ABaseController {

    private static final String FACEIT_USER_URL = "https://www.faceit.com/users/";

    @Autowired
    private ProPlayerRegistry proPlayerRegistry;

    @GetMapping("/{name}/faceit-url")
    public ResponseVO getFaceitUrl(@PathVariable String name) {
        return proPlayerRegistry.playerIdOf(name)
                .map(uuid -> FACEIT_USER_URL + uuid)
                .<ResponseVO>map(this::getSuccessResponseVO)
                .orElseGet(() -> getServerErrorResponseVO("未找到该选手"));
    }
}
