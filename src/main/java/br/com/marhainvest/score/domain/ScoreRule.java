package br.com.marhainvest.score.domain;

import br.com.marhainvest.score.domain.context.ScoreContext;

public interface ScoreRule { ScoreItem evaluate(ScoreContext context); }
