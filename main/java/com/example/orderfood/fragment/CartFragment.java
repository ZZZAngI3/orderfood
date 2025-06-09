package com.example.orderfood.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orderfood.R;
import com.example.orderfood.activity.LoginActivity;
import com.example.orderfood.adapter.CartAdapter;
import com.example.orderfood.entity.CartItem;
import com.example.orderfood.entity.Dish;
import com.example.orderfood.viewmodel.CartViewModel;
import com.example.orderfood.viewmodel.UserViewModel;
import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment implements CartAdapter.OnCartItemListener {
    private RecyclerView rvCartItems;
    private CartAdapter cartAdapter;
    private CartViewModel cartViewModel;
    private UserViewModel userViewModel;
    private TextView tvTotalPrice, tvEmptyCart;
    private CheckBox cbSelectAll;
    private Button btnCheckout;
    private int currentUserId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        initViews(view);
        setupViewModels();
        setupRecyclerView();
        setupListeners();
        checkArguments();
        return view;
    }

    private void initViews(View view) {
        rvCartItems = view.findViewById(R.id.rv_cart_items);
        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        tvEmptyCart = view.findViewById(R.id.tv_empty_cart);
        cbSelectAll = view.findViewById(R.id.cb_select_all);
        btnCheckout = view.findViewById(R.id.btn_checkout);
    }

    private void setupViewModels() {
        // 使用带 SavedStateHandle 的构造函数创建 CartViewModel
        cartViewModel = new ViewModelProvider(this, new SavedStateViewModelFactory(requireActivity().getApplication(), this)).get(CartViewModel.class);
        // 使用 requireActivity() 确保与 MainActivity 使用相同的 UserViewModel 实例
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        Log.d("CartFragment", "UserViewModel created: " + userViewModel);
        Log.d("CartFragment", "CartViewModel created: " + cartViewModel);
        userViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            Log.d("CartFragment", "Current user: " + user);
            if (user != null) {
                currentUserId = user.getId();
                Log.d("CartFragment", "Current user ID: " + currentUserId);
                cartViewModel.initCart(currentUserId);
                observeCartItems();
            } else {
                // 用户未登录，跳转到登录页面
                Toast.makeText(requireContext(), "请先登录", Toast.LENGTH_SHORT).show();
                // 这里应该跳转到登录页面，简化起见不实现
            }
        });
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(requireContext(), new ArrayList<>(), this);
        rvCartItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCartItems.setAdapter(cartAdapter);
    }

    private void setupListeners() {
        cbSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cartViewModel.getCartItemsLiveData().getValue() != null) {
                    for (CartItem item : cartViewModel.getCartItemsLiveData().getValue()) {
                        item.setSelected(isChecked);
                        cartViewModel.updateCartItem(item);
                    }
                }
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkout();
            }
        });
    }

    private void checkArguments() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("dish")) {
            Dish dish = (Dish) args.getSerializable("dish");
            if (dish != null) {
                addDishToCart(dish);
            }
        }
    }

    private void observeCartItems() {
        cartViewModel.getCartItemsLiveData().observe(getViewLifecycleOwner(), new Observer<List<CartItem>>() {
            @Override
            public void onChanged(List<CartItem> cartItems) {
                cartAdapter.setData(cartItems);
                updateUI();
            }
        });
    }

    private void updateUI() {
        List<CartItem> cartItems = cartViewModel.getCartItemsLiveData().getValue();
        if (cartItems == null || cartItems.isEmpty()) {
            rvCartItems.setVisibility(View.GONE);
            tvEmptyCart.setVisibility(View.VISIBLE);
            cbSelectAll.setVisibility(View.GONE);
            btnCheckout.setEnabled(false);
        } else {
            rvCartItems.setVisibility(View.VISIBLE);
            tvEmptyCart.setVisibility(View.GONE);
            cbSelectAll.setVisibility(View.VISIBLE);
            btnCheckout.setEnabled(true);
        }

        // 更新总价
        double totalPrice = cartViewModel.calculateTotalPrice();
        tvTotalPrice.setText("总价: ¥" + String.format("%.2f", totalPrice));

        // 更新全选状态
        boolean allSelected = true;
        if (cartItems != null) {
            for (CartItem item : cartItems) {
                if (!item.isSelected()) {
                    allSelected = false;
                    break;
                }
            }
        }
        cbSelectAll.setChecked(allSelected);
    }

    private void addDishToCart(Dish dish) {
        cartViewModel.addToCart(dish, 1, new CartViewModel.AddToCartCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(requireContext(), "已添加到购物车", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkout() {
        List<CartItem> selectedItems = new ArrayList<>();
        if (cartViewModel.getCartItemsLiveData().getValue() != null) {
            for (CartItem item : cartViewModel.getCartItemsLiveData().getValue()) {
                if (item.isSelected()) {
                    selectedItems.add(item);
                }
            }
        }

        if (selectedItems.isEmpty()) {
            Toast.makeText(requireContext(), "请选择要结算的商品", Toast.LENGTH_SHORT).show();
            return;
        }

        // 检查库存
        boolean hasEnoughStock = true;
        for (CartItem item : selectedItems) {
            // 假设 Dish 类中有 stock 属性表示库存
            if (item.getCount() > item.getStock()) {
                hasEnoughStock = false;
                Toast.makeText(requireContext(), item.getDishName() + " 库存不足", Toast.LENGTH_SHORT).show();
                break;
            }
        }

        if (hasEnoughStock) {
            // 生成订单
            generateOrder(selectedItems);

            // 更新库存
            updateStock(selectedItems);

            // 清空购物车
            cartViewModel.clearCart();

            Toast.makeText(requireContext(), "结算成功", Toast.LENGTH_SHORT).show();
        }
    }

    private void generateOrder(List<CartItem> selectedItems) {
        // 这里可以实现生成订单的逻辑，例如将订单信息保存到数据库
        // 简化起见，暂时不实现具体逻辑
    }

    private void updateStock(List<CartItem> selectedItems) {
        // 这里可以实现更新库存的逻辑，例如更新 Dish 表中的 stock 字段
        // 简化起见，暂时不实现具体逻辑
    }

    @Override
    public void onItemCheckedChanged(CartItem cartItem, boolean isChecked) {
        cartItem.setSelected(isChecked);
        cartViewModel.updateCartItem(cartItem);
    }

    @Override
    public void onQuantityChanged(CartItem cartItem) {
        cartViewModel.updateCartItem(cartItem);
    }

    @Override
    public void onDeleteItem(CartItem cartItem) {
        cartViewModel.deleteCartItem(cartItem);
    }
}


