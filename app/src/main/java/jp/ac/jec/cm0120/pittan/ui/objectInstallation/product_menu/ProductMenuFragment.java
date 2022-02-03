package jp.ac.jec.cm0120.pittan.ui.objectInstallation.product_menu;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import jp.ac.jec.cm0120.pittan.R;

public class ProductMenuFragment extends Fragment {

  /// Interface
  public interface OnClickRecyclerViewListener {
    void onClickRecyclerItem(String textureName);
  }

  /// Constant
  private static final String TAG = "###";
  /// Components
  private RecyclerView productMenuRecyclerView;

  /// Fields
  private ArrayList<ProductMenuModel> productMenuArrayList;
  private ProductMenuRecyclerViewAdapter productMenuAdapter;
  private OnClickRecyclerViewListener mOnClickRecyclerViewListener;
  private ArrayList<String> productMenuCurtainName = new ArrayList<>();

  public ProductMenuFragment() {
    // Required empty public constructor
  }

  public static ProductMenuFragment newInstance() {
    return new ProductMenuFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_product_menu, container, false);
    productMenuRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_product_menu_double_opening);
    addProductData();
    buildProductMenuRecyclerView();
    return view;
  }

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);
    if (context instanceof OnClickRecyclerViewListener) {
      mOnClickRecyclerViewListener = (OnClickRecyclerViewListener) context;
    }
  }

  private void addProductData() {
    productMenuCurtainName = getProductName();
    productMenuArrayList = getProductMenuData(productMenuCurtainName);
  }

  private ArrayList<ProductMenuModel> getProductMenuData(ArrayList<String> textureNames) {
    ArrayList<ProductMenuModel> ary = new ArrayList<>();
    for (String textureName :textureNames) {
      ProductMenuModel tmp = new ProductMenuModel();
      tmp.setItemTextureName(textureName);
      tmp.setItemTextureImage(getBitmapTexture(textureName));
      ary.add(tmp);
    }
    return ary;
  }

  private Bitmap getBitmapTexture(String textureName) {
    AssetManager assets = getResources().getAssets();
    InputStream inputStream = null;
    try {
      inputStream = assets.open("textures/"+textureName);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return  BitmapFactory.decodeStream(inputStream);
  }

  /// Load the name from Assets/textures
  private ArrayList<String> getProductName() {
    ArrayList<String> nameArrayList = new ArrayList<>();
    AssetManager assetManager = getResources().getAssets();
    String[] fileNames = null;
    try {
      fileNames = assetManager.list("textures");
      nameArrayList.addAll(Arrays.asList(fileNames));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return nameArrayList;
  }

  private void buildProductMenuRecyclerView() {
    productMenuAdapter = new ProductMenuRecyclerViewAdapter(this.getContext(), productMenuArrayList);
    productMenuRecyclerView.setAdapter(productMenuAdapter);
    productMenuRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
    productMenuAdapter.setOnItemClickListener(new ProductMenuRecyclerViewAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(String textureName) {
        if (mOnClickRecyclerViewListener != null) {
          mOnClickRecyclerViewListener.onClickRecyclerItem(textureName);
        }
      }
    });
  }


}