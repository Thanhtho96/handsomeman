package com.tt.handsomeman.ui.handyman.more;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.adapter.CategorySelectionAdapter;
import com.tt.handsomeman.databinding.ActivityAddNewSkillBinding;
import com.tt.handsomeman.model.Category;
import com.tt.handsomeman.model.Skill;
import com.tt.handsomeman.response.ListCategory;
import com.tt.handsomeman.ui.BaseAppCompatActivityWithViewModel;
import com.tt.handsomeman.util.CustomDividerItemDecoration;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.viewmodel.HandymanViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class AddNewSkill extends BaseAppCompatActivityWithViewModel<HandymanViewModel> {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private EditText edtNewSkillName;
    private ImageButton imCheckAddSkill;
    private RecyclerView rcvSkillCategoriesName;
    private CategorySelectionAdapter addNewSkillAdapter;
    private List<Category> categoryList = new ArrayList<>();
    private List<Skill> skillEditList = new ArrayList<>();
    private ActivityAddNewSkillBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewSkillBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        HandymanApp.getComponent().inject(this);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(HandymanViewModel.class);

        edtNewSkillName = binding.editTextNewSkillName;
        rcvSkillCategoriesName = binding.recyclerViewSkillNameCategories;
        imCheckAddSkill = binding.imageButtonCheckAddNewSkill;

        binding.addNewSkillBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        createCategoryRecyclerView();
        getListCategory();

        skillEditList = (List<Skill>) getIntent().getSerializableExtra("listSkill");

        imCheckAddSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtNewSkillName.getText().toString().trim().matches("") && addNewSkillAdapter.getSelected() != null) {
                    Category category = addNewSkillAdapter.getSelected();
                    Skill skill = new Skill(category.getCategory_id(), edtNewSkillName.getText().toString().trim());

                    if (skillEditList.contains(skill)) {
                        Toast.makeText(AddNewSkill.this, getString(R.string.duplicate_skill), Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("skillAdded", skill);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            }
        });
    }

    private void createCategoryRecyclerView() {
        addNewSkillAdapter = new CategorySelectionAdapter(this, categoryList);
        RecyclerView.LayoutManager layoutManagerPayout = new LinearLayoutManager(this);
        rcvSkillCategoriesName.setLayoutManager(layoutManagerPayout);
        rcvSkillCategoriesName.setItemAnimator(new DefaultItemAnimator());
        rcvSkillCategoriesName.addItemDecoration(new CustomDividerItemDecoration(getResources().getDrawable(R.drawable.recycler_view_divider)));
        rcvSkillCategoriesName.setAdapter(addNewSkillAdapter);
    }

    private void getListCategory() {
        String authorizationCode = sharedPreferencesUtils.get("token", String.class);
        baseViewModel.fetchListCategory(authorizationCode);
        baseViewModel.getListCategoryMutableLiveData().observe(this, new Observer<ListCategory>() {
            @Override
            public void onChanged(ListCategory listCategory) {
                categoryList.clear();
                categoryList.addAll(listCategory.getCategoryList());
                addNewSkillAdapter.notifyDataSetChanged();
            }
        });
    }
}
