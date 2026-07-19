package br.com.marhainvest.score.domain.context;

import br.com.marhainvest.asset.domain.AssetSnapshot;

public interface ScoreContext {
    AssetSnapshot asset();
}