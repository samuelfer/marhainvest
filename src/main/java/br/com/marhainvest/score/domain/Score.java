package br.com.marhainvest.score.domain;
import java.util.List;
public record Score(int total, List<ScoreItem> items) { public Score { items = List.copyOf(items); } }
