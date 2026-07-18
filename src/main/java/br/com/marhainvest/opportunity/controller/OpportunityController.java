package br.com.marhainvest.opportunity.controller;

import br.com.marhainvest.asset.domain.AssetType;
import br.com.marhainvest.opportunity.application.OpportunityService;
import br.com.marhainvest.opportunity.domain.OpportunityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/opportunities")
@RequiredArgsConstructor
public class OpportunityController {

    private final OpportunityService service;

    @GetMapping
    public List<OpportunityResponse> findAll(
            @RequestParam(required = false) AssetType assetType) {
        return service.findAll(assetType);
    }

}