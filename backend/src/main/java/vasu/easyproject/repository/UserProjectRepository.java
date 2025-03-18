package vasu.easyproject.repository;

import vasu.easyproject.model.UserProject;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProjectRepository extends JpaRepository<UserProject, Long> {

    UserProject findByUserIdAndProjectId(Long userId, Long projectId);
    List<UserProject> findByProjectId(Long projectId);
}
