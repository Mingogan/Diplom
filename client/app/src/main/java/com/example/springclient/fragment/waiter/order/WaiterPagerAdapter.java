package com.example.springclient.fragment.waiter.order;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class WaiterPagerAdapter extends FragmentStateAdapter {

    public WaiterPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new WaiterMyOrdersFragment();
        } else {
            return new WaiterAllOrdersFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
