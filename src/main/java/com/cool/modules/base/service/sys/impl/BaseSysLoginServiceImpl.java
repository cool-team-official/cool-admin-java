package com.cool.modules.base.service.sys.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.GifCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import com.cool.core.cache.CoolCache;
import com.cool.core.exception.CoolException;
import com.cool.core.exception.CoolPreconditions;
import com.cool.core.security.jwt.JwtTokenUtil;
import com.cool.modules.base.dto.sys.BaseSysLoginDto;
import com.cool.modules.base.entity.sys.BaseSysUserEntity;
import com.cool.modules.base.mapper.sys.BaseSysUserMapper;
import com.cool.modules.base.security.CoolSecurityUtil;
import com.cool.modules.base.service.sys.BaseSysLoginService;
import com.cool.modules.base.service.sys.BaseSysPermsService;
import com.mybatisflex.core.query.QueryWrapper;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BaseSysLoginServiceImpl implements BaseSysLoginService {

	private final CoolCache coolCache;

	private final AuthenticationManager authenticationManager;

	private final CoolSecurityUtil coolSecurityUtil;

	private final JwtTokenUtil jwtTokenUtil;

	private final BaseSysUserMapper baseSysUserMapper;

	private final BaseSysPermsService baseSysPermsService;

	@Override
	public Object captcha(String type, Integer width, Integer height) {
		// 1、生成验证码 2、生成对应的ID并设置在缓存中，验证码过期时间30分钟；
		Map<String, Object> result = new HashMap<>();
		String captchaId = StrUtil.uuid();
		result.put("captchaId", captchaId);
		RandomGenerator randomGenerator = new RandomGenerator(4);
		GifCaptcha gifCaptcha = CaptchaUtil.createGifCaptcha(width, height);
		gifCaptcha.setGenerator(randomGenerator);
		gifCaptcha.setBackground(new Color(248, 248, 248));
		gifCaptcha.setMaxColor(60);
		gifCaptcha.setMinColor(55);
		result.put("data", "data:image/png;base64," + gifCaptcha.getImageBase64());
		coolCache.set("verify:img:" + captchaId, gifCaptcha.getCode(), 1800);
		return result;
	}

	@Override
	public Object login(BaseSysLoginDto baseSysLoginDto) {
		// 1、检查验证码是否正确 2、执行登录操作
		String verifyCode = coolCache.get("verify:img:" + baseSysLoginDto.getCaptchaId(),
			String.class);
		if (StrUtil.isNotEmpty(verifyCode)
			&& verifyCode.equalsIgnoreCase(baseSysLoginDto.getVerifyCode())) {
			UsernamePasswordAuthenticationToken upToken =
				new UsernamePasswordAuthenticationToken(
					baseSysLoginDto.getUsername(), baseSysLoginDto.getPassword());
			Authentication authentication = authenticationManager.authenticate(upToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			// 查询用户信息并生成token
			BaseSysUserEntity sysUserEntity =
				baseSysUserMapper.selectOneByQuery(
					QueryWrapper.create()
						.eq(BaseSysUserEntity::getUsername, baseSysLoginDto.getUsername()));
			CoolPreconditions.check(
				ObjectUtil.isEmpty(sysUserEntity) || sysUserEntity.getStatus() == 0, "用户已禁用");
			Long[] roleIds = baseSysPermsService.getRoles(sysUserEntity);
			Dict tokenInfo =
				Dict.create()
					.set("roleIds", roleIds)
					.set("username", baseSysLoginDto.getUsername())
					.set("userId", sysUserEntity.getId())
					.set("passwordVersion", sysUserEntity.getPasswordV());
			String token = jwtTokenUtil.generateToken(tokenInfo);
			String refreshToken = jwtTokenUtil.generateRefreshToken(tokenInfo);
			coolCache.del("verify:img:" + baseSysLoginDto.getCaptchaId());
			return Dict.create()
				.set("token", token)
				.set("expire", jwtTokenUtil.getExpire())
				.set("refreshToken", refreshToken)
				.set("refreshExpire", jwtTokenUtil.getRefreshExpire());
		} else {
			coolCache.del("verify:img:" + baseSysLoginDto.getCaptchaId());
			throw new CoolException("验证码不正确");
		}
	}

	@Override
	public void logout(Long adminUserId, String username) {
		coolSecurityUtil.logout(adminUserId, username);
	}

	@Override
	public Object refreshToken(String refreshToken) {
		JWT jwt = jwtTokenUtil.getTokenInfo(refreshToken);
		try {
			CoolPreconditions.check(jwt == null || !(Boolean) jwt.getPayload("isRefresh"),
				"错误的token");

			BaseSysUserEntity baseSysUserEntity =
				baseSysUserMapper.selectOneById(Convert.toLong(jwt.getPayload("userId")));
			Long[] roleIds = baseSysPermsService.getRoles(baseSysUserEntity);
			Dict tokenInfo =
				Dict.create()
					.set("roleIds", roleIds)
					.set("username", baseSysUserEntity.getUsername())
					.set("userId", baseSysUserEntity.getId())
					.set("passwordVersion", baseSysUserEntity.getPasswordV());
			String token = jwtTokenUtil.generateToken(tokenInfo);
			refreshToken = jwtTokenUtil.generateRefreshToken(tokenInfo);
			return Dict.create()
				.set("token", token)
				.set("expire", jwtTokenUtil.getExpire())
				.set("refreshToken", refreshToken)
				.set("refreshExpire", jwtTokenUtil.getRefreshExpire());
		} catch (Exception e) {
			throw new CoolException("错误的token", e);
		}
	}
}
