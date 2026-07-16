package com.bookhaven.cart.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookhaven.cart.entity.CartItem;
import com.bookhaven.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;

    public List<CartItem> getCartItems(Long userId) {
        return cartRepository.selectList(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getUserId, userId)
                        .orderByDesc(CartItem::getCreateTime));
    }

    public CartItem addItem(Long userId, Long bookId, String bookTitle,
                            String bookCover, java.math.BigDecimal bookPrice, Integer quantity) {
        // 检查是否已存在
        CartItem existing = cartRepository.selectOne(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getUserId, userId)
                        .eq(CartItem::getBookId, bookId));
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            cartRepository.updateById(existing);
            return existing;
        }
        CartItem item = new CartItem();
        item.setUserId(userId);
        item.setBookId(bookId);
        item.setBookTitle(bookTitle);
        item.setBookCover(bookCover);
        item.setBookPrice(bookPrice);
        item.setQuantity(quantity);
        cartRepository.insert(item);
        return item;
    }

    public void updateQuantity(Long id, Integer quantity) {
        CartItem item = cartRepository.selectById(id);
        if (item != null) {
            item.setQuantity(quantity);
            cartRepository.updateById(item);
        }
    }

    public void removeItem(Long id) {
        cartRepository.deleteById(id);
    }

    public void clearCart(Long userId) {
        cartRepository.delete(new LambdaQueryWrapper<CartItem>().eq(CartItem::getUserId, userId));
    }

    public List<CartItem> getSelectedItems(Long userId, List<Long> ids) {
        return cartRepository.selectList(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getUserId, userId)
                        .in(CartItem::getId, ids));
    }
}
