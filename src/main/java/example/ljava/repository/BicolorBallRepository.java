package example.ljava.repository;

import java.util.Optional;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import example.ljava.entity.po.BicolorBall;

public interface BicolorBallRepository extends PagingAndSortingRepository<BicolorBall, Long>, QuerydslPredicateExecutor<BicolorBall> {
    Optional<BicolorBall> findFirstByStageIgnoreCase(String stage);
}
