package br.com.marhainvest.recommendation.api;

import br.com.marhainvest.portfolio.application.PortfolioLoader;
import br.com.marhainvest.recommendation.application.RecommendationEngine;
import br.com.marhainvest.recommendation.domain.Recommendation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {

 private final RecommendationEngine recommendationEngine;
 private final PortfolioLoader portfolioLoader;

 @PostMapping
 public ResponseEntity<List<Recommendation>> recommend(
         @Valid
         @RequestBody PortfolioRecommendationRequest request) {

  var portfolio = portfolioLoader.load(
          request.portfolioId()
  );

  var recommendations =
          recommendationEngine.recommend(
                  portfolio,
                  request.availableMoney()
          );

  return ResponseEntity.ok(recommendations);
 }
}