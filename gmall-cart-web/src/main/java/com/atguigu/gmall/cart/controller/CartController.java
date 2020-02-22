package com.atguigu.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.annotations.LoginRequired;
import com.atguigu.gmall.bean.OmsCartItem;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.service.cart.CartService;
import com.atguigu.gmall.service.pms.SkuService;
import com.atguigu.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CartController {

    @Reference
    private SkuService skuService;

    @Reference
    private CartService cartService;



    @RequestMapping("checkCart")
    @LoginRequired(loginSuccess = false)
    public String checkCart(String isChecked, String skuId, HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap modelMap) {

        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");

        // 调用服务，修改状态
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setIsChecked(isChecked);
        cartService.checkCart(omsCartItem);

        // 将最新的数据从缓存中查出，渲染给内嵌页
        List<OmsCartItem> omsCartItems = cartService.cartList(memberId);
        modelMap.put("cartList", omsCartItems);
        BigDecimal totalAmount = getTotalAmount(omsCartItems);
        modelMap.put("totalAmount", totalAmount);
        return "cartListInner";
    }


    @RequestMapping("cartList")
    public String cartList(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap modelMap) {

        List<OmsCartItem> omsCartItems = new ArrayList<>();
        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");

        if (StringUtils.isNotBlank(memberId)) {
            // 已经登录查询db
            omsCartItems = cartService.cartList(memberId);
        } else {
            // 没有登录查询cookie
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isNotBlank(cartListCookie)) {
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
            }
        }

        for (OmsCartItem omsCartItem : omsCartItems) {
            omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
        }

        modelMap.put("cartList", omsCartItems);
        BigDecimal totalAmount = getTotalAmount(omsCartItems);
        modelMap.put("totalAmount", totalAmount);
        return "cartList";
    }


    private BigDecimal getTotalAmount(List<OmsCartItem> omsCartItemList) {
        BigDecimal totalAmount = new BigDecimal("0");
        for (OmsCartItem omsCartItem : omsCartItemList) {
            BigDecimal totalPrice = omsCartItem.getTotalPrice();
            if (omsCartItem.getIsChecked().equals("1")) {
                totalAmount = totalAmount.add(totalPrice);
            }
        }

        return totalAmount;
    }


    @RequestMapping("addToCart")//添加购物车
    public String addToCart(String skuId, int quantity, HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        //购物车列表
        List<OmsCartItem> omsCartItemList = new ArrayList<>();


        //调用商品服务查询商品信息
        PmsSkuInfo skuInfo = skuService.getSkuById(skuId);

        //将商品信息封装成购物车信息
        OmsCartItem cartItem = new OmsCartItem();
        cartItem.setCreateDate(new Date());
        cartItem.setDeleteStatus(0);
        cartItem.setModifyDate(new Date());
        cartItem.setPrice(skuInfo.getPrice());
        cartItem.setProductAttr("");
        cartItem.setProductBrand("");
        cartItem.setProductCategoryId(skuInfo.getCatalog3Id());
        cartItem.setProductId(skuInfo.getProductId());
        cartItem.setProductName(skuInfo.getSkuName());
        cartItem.setProductPic(skuInfo.getSkuDefaultImg());
        cartItem.setProductSkuCode("11111111111");
        cartItem.setProductSkuId(skuId);
        cartItem.setQuantity(new BigDecimal(quantity));

        //判断用户是否登录
        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");

        //如果用户没有登录
        if (StringUtils.isBlank(memberId)) {
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isBlank(cartListCookie)) {
                //如果cookie为空的话
                omsCartItemList.add(cartItem);
            } else {
                //cookie不为空,就解析它
                omsCartItemList = JSON.parseArray(cartListCookie, OmsCartItem.class);
                boolean exist = if_cart_exist(omsCartItemList, cartItem);

                if (exist) {
                    //之前添加过，更新购物车数量
                    for (OmsCartItem omsCartItem : omsCartItemList) {
                        if (omsCartItem.getProductSkuId().equals(cartItem.getProductSkuId())) {
                            omsCartItem.setQuantity(omsCartItem.getQuantity().add(cartItem.getQuantity()));
                        }
                    }
                } else {
                    //如果之前没有添加，新增到当前的购物车
                    omsCartItemList.add(cartItem);
                }

            }


        } else {//如果用户已登录
            //从DB中查出购物车数据
            OmsCartItem omsCartItemFromDb = cartService.ifCartExistByUser(memberId, skuId);

            if (omsCartItemFromDb == null) {
                //该用户没有添加过当前商品，就添加上
                cartItem.setMemberId(memberId);
                cartItem.setMemberNickname("小凯");
                cartItem.setQuantity(new BigDecimal(quantity));
                cartService.addCart(cartItem);
            } else {
                //该用户之前添加过该商品
                omsCartItemFromDb.setQuantity(omsCartItemFromDb.getQuantity().add(cartItem.getQuantity()));
                cartService.updateCart(omsCartItemFromDb);
            }

            //同步缓存
            cartService.flushCartCache(memberId);
        }
        return "redirect:/success.html";
    }


    private boolean if_cart_exist(List<OmsCartItem> cartItemList, OmsCartItem omsCartItem) {
//        return  cartItemList.contains(omsCartItem);
        boolean b = false;
        for (OmsCartItem cartItem : cartItemList) {
            String productSkudId = omsCartItem.getProductSkuId();
            if (productSkudId.equals(omsCartItem.getProductSkuId())) {
                b = true;
            }
        }

        return b;

    }
}
