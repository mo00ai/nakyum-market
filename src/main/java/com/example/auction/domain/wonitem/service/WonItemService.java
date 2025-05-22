package com.example.auction.domain.wonitem.service;

import static com.example.auction.domain.user.exception.ErrorCode.NOT_FOUND_USER;
import static com.example.auction.domain.wonitem.exception.WonItemErrorCode.UNAUTHORIZED_WON_ITEM;

import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.auction.common.exception.CustomException;
import com.example.auction.common.response.PageResponse;
import com.example.auction.common.service.RedisService;
import com.example.auction.domain.product.entity.Product;
import com.example.auction.domain.user.entity.User;
import com.example.auction.domain.user.repository.UserRepository;
import com.example.auction.domain.wonitem.dto.response.WonItemResponseDto;
import com.example.auction.domain.wonitem.entity.WonItem;

@Service
@RequiredArgsConstructor
public class WonItemService {

	public final static String WON_ITEM_PREFIX = "wonItem:";

	private final UserRepository userRepository;
	private final RedisService redisService;

	public void createWonItem(Product product, User user) {

		String key = WON_ITEM_PREFIX + user.getId() + ":" + product.getId();

		WonItem wonItem = WonItem.of(key, product, user);

		redisService.setKeyValue(key, wonItem, Duration.ofSeconds(120));

	}

	@Transactional
	public PageResponse<WonItemResponseDto> findWonItems(User loginUser, int page, int size) {

		User findUser = userRepository.findById(loginUser.getId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_USER, NOT_FOUND_USER.getMessage()));

		Set<String> keys = Optional.ofNullable(redisService.scanKeys(WON_ITEM_PREFIX + findUser.getId() + ":*"))
			.orElse(Collections.emptySet());

		List<WonItemResponseDto> wonItemResponseDto = keys.stream()
			.map(key -> {
				WonItem wonItem = (WonItem)redisService.getKeyValue(key);
				Long ttl = redisService.getExpire(key);

				return WonItemResponseDto.from(wonItem, ttl);
			})
			.filter(Objects::nonNull)
			.sorted(Comparator.comparing(WonItemResponseDto::getTtl).reversed())
			.toList();

		return PageResponse.fromRedis(wonItemResponseDto, page, size);
	}

	public void validateMyWonItem(Long userId, List<Long> productIds) {
		for (Long productId : productIds) {
			String key = WON_ITEM_PREFIX + userId + ":" + productId;
			WonItem wonItem = (WonItem)redisService.getKeyValue(key);
			if (wonItem == null) {
				throw new CustomException(UNAUTHORIZED_WON_ITEM, UNAUTHORIZED_WON_ITEM.getMessage());
			}
		}
	}

	public void deleteWonItems(Long userId, List<Long> productIds) {
		productIds.forEach(productId -> {
			String key = WON_ITEM_PREFIX + userId + ":" + productId;
			redisService.deleteKeyValue(key);
		});
	}

}
