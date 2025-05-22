package com.example.auction.domain.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.auction.domain.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

	@Query("select p from Product p join fetch p.image where p.id = :id")
	Optional<Product> findByIdWithImage(@Param("id") Long id);

	List<Product> findAllByIdInAndFinalPriceIsNotNull(List<Long> productIds);

	Optional<Product> findAllByIdAndFinalPriceIsNotNull(Long productId);
}
