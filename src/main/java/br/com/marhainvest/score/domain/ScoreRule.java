package br.com.marhainvest.score.domain;

import br.com.marhainvest.score.domain.context.RecommendationContext;
public interface ScoreRule { ScoreItem evaluate(RecommendationContext context); }
