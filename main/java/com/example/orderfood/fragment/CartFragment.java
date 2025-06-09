package com.example.orderfood.fragment;

import android.os.Bundle;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orderfood.R;
import com.example.orderfood.adapter.CartAdapter;
import com.example.orderfood.entity.CartItem;
import com.example.orderfood.entity.Dish;
import com.example.orderfood.viewmodel.CartViewModel;
import com.example.orderfood.viewmodel.UserViewModel;
import com.example.orderfood.util.UserSession;
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
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        int userId = UserSession.getUserId(requireContext());
        if (userId != -1) {
            currentUserId = userId;
            cartViewModel.initCart(currentUserId);
            observeCartItems();
        } else {
            Toast.makeText(requireContext(), "请先登录", Toast.LENGTH_SHORT).show();
        }
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
            if (item.getCount() > item.getStock()) {
                hasEnoughStock = false;
                Toast.makeText(requireContext(), item.getDishName() + " 库存不足", Toast.LENGTH_SHORT).show();
                break;
            }
        }

        if (hasEnoughStock) {
            generateOrder(selectedItems);
            updateStock(selectedItems);
            cartViewModel.clearCart();
            Toast.makeText(requireContext(), "结算成功", Toast.LENGTH_SHORT).show();
        }
    }

    private void generateOrder(List<CartItem> selectedItems) {
        // 订单生成逻辑
    }

    private void updateStock(List<CartItem> selectedItems) {
        // 更新库存逻辑
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


