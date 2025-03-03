package com.asiainfo.mcp.tmc.sso.authorize;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

/**
 * Security的配置
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationTokenFilter authenticationTokenFilter;
    @Autowired
    private SsoProperties ssoProperties;

    /**
     * 定义安全策略
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        this.configWebSecurityWhiteList(http);
                http
                //关闭csrf
                .csrf().disable()
                //不通过Session获取SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 对于登录接口 允许匿名访问
                .antMatchers("/**").permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().permitAll();
        //把token校验过滤器添加到过滤器链中
        http.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //  白名单
    private void configWebSecurityWhiteList(HttpSecurity http) throws Exception {
        String whiteList = ssoProperties.getWhiteUrlList();
        if (!StringUtils.isEmpty(whiteList)) {
            String[] whiteArray = whiteList.split(",");
            ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) http.authorizeRequests().regexMatchers(".*__needlogin.*")).authenticated();
            ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) http.authorizeRequests().antMatchers(whiteArray)).permitAll();
        }

    }
}
