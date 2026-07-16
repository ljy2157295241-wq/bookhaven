package com.bookhaven.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookhaven.common.exception.BusinessException;
import com.bookhaven.common.model.PageDTO;
import com.bookhaven.product.entity.Book;
import com.bookhaven.product.entity.Category;
import com.bookhaven.product.repository.BookRepository;
import com.bookhaven.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    // ========== Book CRUD ==========

    public PageDTO<Book> searchBooks(String keyword, Long categoryId, int page, int size) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<Book>()
                .eq(Book::getStatus, "ON_SHELF")
                .and(keyword != null && !keyword.isEmpty(), w -> w
                        .like(Book::getTitle, keyword)
                        .or().like(Book::getAuthor, keyword)
                        .or().like(Book::getIsbn, keyword))
                .eq(categoryId != null, Book::getCategoryId, categoryId)
                .orderByDesc(Book::getSales);

        Page<Book> pageResult = bookRepository.selectPage(new Page<>(page, size), wrapper);
        return new PageDTO<>(pageResult.getRecords(), pageResult.getTotal(), page, size);
    }

    public Book getBookById(Long id) {
        Book book = bookRepository.selectById(id);
        if (book == null) {
            throw new BusinessException("Book not found");
        }
        return book;
    }

    public Book addBook(Book book) {
        book.setStatus("ON_SHELF");
        book.setSales(0);
        bookRepository.insert(book);
        return book;
    }

    public Book updateBook(Book book) {
        bookRepository.updateById(book);
        return bookRepository.selectById(book.getId());
    }

    @Transactional
    public boolean deductStock(Long bookId, Integer quantity) {
        Book book = bookRepository.selectById(bookId);
        if (book == null || book.getStock() < quantity) {
            throw new BusinessException("Insufficient stock");
        }
        book.setStock(book.getStock() - quantity);
        book.setSales(book.getSales() + quantity);
        return bookRepository.updateById(book) > 0;
    }

    @Transactional
    public boolean restoreStock(Long bookId, Integer quantity) {
        Book book = bookRepository.selectById(bookId);
        book.setStock(book.getStock() + quantity);
        book.setSales(book.getSales() - quantity);
        return bookRepository.updateById(book) > 0;
    }

    // ========== Category ==========

    public List<Category> getAllCategories() {
        return categoryRepository.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort));
    }
}
