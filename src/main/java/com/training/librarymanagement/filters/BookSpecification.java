package com.training.librarymanagement.filters;

import com.training.librarymanagement.entities.Book;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class BookSpecification implements Specification<Book> {

    private FilterBook filter;

    public BookSpecification(FilterBook filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<Book> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
        Predicate predicate = cb.conjunction();
        if (filter.getTitle() != null) {
            predicate.getExpressions().add(cb.equal(root.get("title"), filter.getTitle()));
        }
        if (filter.getCategory() != null) {
            predicate.getExpressions().add(cb.equal(root.get("subjectCategory"), filter.getCategory()));
        }

        if (filter.getPublicationDate() != null) {
            predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("publicationDate"), filter.getPublicationDate()));
        }
        if (filter.getAuthorName() != null) {
            predicate.getExpressions().add(cb.equal(root.get("author").get("lastName"), filter.getAuthorName()));
        }

        return predicate;
    }
}
