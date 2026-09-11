package fr._42.educationcenter.repositories;

import fr._42.educationcenter.models.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    Page<Lesson> findByCourseId(Long courseId, Pageable pageable);
}
