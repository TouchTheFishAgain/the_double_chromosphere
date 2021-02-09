package example.ljava.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import example.ljava.entity.BicolorBall;

public interface BicolorBallRepository extends PagingAndSortingRepository<BicolorBall, Long> {
}
