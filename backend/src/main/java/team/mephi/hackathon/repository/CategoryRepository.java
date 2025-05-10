package team.mephi.hackathon.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import team.mephi.hackathon.entity.Category;

@Repository
public interface CategoryRepository
    extends JpaRepository<Category, UUID>, JpaSpecificationExecutor<Category> {}
