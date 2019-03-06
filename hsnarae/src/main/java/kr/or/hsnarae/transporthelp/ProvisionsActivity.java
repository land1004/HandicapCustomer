package kr.or.hsnarae.transporthelp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;

import kr.or.hsnarae.transporthelp.common.FontAwesome.CFontAwesomeManager;
import kr.or.hsnarae.transporthelp.impl.preference.ConfigPreference;

public class ProvisionsActivity extends Activity implements View.OnClickListener
{
    private final String THIS_TAG = "ProvisionsActivity";

    private CheckBox chekProvision1, chekProvision2,chekProvision3,chekProvision4,chekProvision5;
    private CheckBox chekProvisionAll;

    private boolean mOnlyProvision = false;
    private boolean mRegistry = true;
    private String mIDPWSearch="";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provisions);

        // 기본 설정
        TextView btnBack = (TextView)findViewById(R.id.btnBack);
        btnBack.setTypeface(CFontAwesomeManager.getTypeface(this, CFontAwesomeManager.FONTAWESOME));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LinearLayout layoutBTNLeft = (LinearLayout)findViewById(R.id.layoutBTNLeft);
        LinearLayout layoutBTNCenter = (LinearLayout)findViewById(R.id.layoutBTNCenter);
        LinearLayout layoutBTNRight = (LinearLayout)findViewById(R.id.layoutBTNRight);

        Button bstBTNLeft = (Button)findViewById(R.id.btnLeft);
        Button bstBTNCenter = (Button)findViewById(R.id.btnCenter);
        Button bstBTNRight = (Button)findViewById(R.id.btnRight);

        layoutBTNLeft.setVisibility(View.GONE);
        layoutBTNCenter.setVisibility(View.VISIBLE);
        layoutBTNRight.setVisibility(View.GONE);

        bstBTNCenter.setOnClickListener(this);
        bstBTNCenter.setText("확 인");

        Intent intent = getIntent();
        mOnlyProvision = intent.getBooleanExtra("type", false);
        mRegistry  = intent.getBooleanExtra("registry", true);
        mIDPWSearch = intent.getStringExtra("idpw_search");
        //////////////////////////////////////////////////////////////

        // 화성나래 동의
        BootstrapButton bstBtn = (BootstrapButton)findViewById(R.id.btnProvision1);
        bstBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProvisionsActivity.this, ProvisionViewActivity.class);
                intent.putExtra("url", getString(R.string.provision_view1));
                intent.putExtra("title", "서약서");
                startActivity(intent);
            }
        });

        // 서비스 이용
        bstBtn = (BootstrapButton)findViewById(R.id.btnProvision2);
        bstBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProvisionsActivity.this, ProvisionViewActivity.class);
                intent.putExtra("url", getString(R.string.provision_view2));
                intent.putExtra("title", "서비스 이용약관");
                startActivity(intent);
            }
        });

        // 개인정보
        bstBtn = (BootstrapButton)findViewById(R.id.btnProvision3);
        bstBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProvisionsActivity.this, ProvisionViewActivity.class);
                intent.putExtra("url", getString(R.string.provision_view3));
                intent.putExtra("title", "개인정보 취급방침");
                startActivity(intent);
            }
        });

        // 위치기반
        bstBtn = (BootstrapButton)findViewById(R.id.btnProvision4);
        bstBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProvisionsActivity.this, ProvisionViewActivity.class);
                intent.putExtra("url", getString(R.string.provision_view4));
                intent.putExtra("title", "위치기반 서비스 약관");
                startActivity(intent);
            }
        });

        // 고유식별정보 수집 동의
        bstBtn = (BootstrapButton)findViewById(R.id.btnProvision5);
        bstBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProvisionsActivity.this, ProvisionViewActivity.class);
                intent.putExtra("url", getString(R.string.provision_view5));
                intent.putExtra("title", "고유식별정보 수집 동의");
                startActivity(intent);
            }
        });


        chekProvision1 = (CheckBox)findViewById(R.id.checkProvision1);
        chekProvision1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doCheckAllButton ();
            }
        });
        chekProvision2 = (CheckBox)findViewById(R.id.checkProvision2);
        chekProvision2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doCheckAllButton ();
            }
        });
        chekProvision3 = (CheckBox)findViewById(R.id.checkProvision3);
        chekProvision3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doCheckAllButton ();
            }
        });
        chekProvision4 = (CheckBox)findViewById(R.id.checkProvision4);
        chekProvision4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doCheckAllButton ();
            }
        });
        chekProvision5 = (CheckBox)findViewById(R.id.checkProvision5);
        chekProvision5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doCheckAllButton ();
            }
        });

        chekProvisionAll = (CheckBox)findViewById(R.id.checkProvisionAll);
        chekProvisionAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chekProvision1.setChecked(chekProvisionAll.isChecked());
                chekProvision2.setChecked(chekProvisionAll.isChecked());
                chekProvision3.setChecked(chekProvisionAll.isChecked());
                chekProvision4.setChecked(chekProvisionAll.isChecked());
                chekProvision5.setChecked(chekProvisionAll.isChecked());
            }
        });
    }

    /**
     * 약관 모두 동의 체크버튼 처리
     */
    private void doCheckAllButton ()
    {
        if (chekProvision1.isChecked() && chekProvision2.isChecked() &&
                chekProvision3.isChecked() && chekProvision4.isChecked() && chekProvision5.isChecked())
            chekProvisionAll.setChecked(true);
        else
            chekProvisionAll.setChecked(false);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnCenter:    // 확인
                doProvision();
                break;
        }
    }

    /**
     * 동의 여부 확인
     */
    private void doProvision()
    {
        if (!chekProvision1.isChecked())
        {
            Toast.makeText(getBaseContext(), "화성나래서비스 서약서 동의서에 동의해주십시요.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!chekProvision2.isChecked())
        {
            Toast.makeText(getBaseContext(), "서비스 이용약관에 동의해주십시요.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!chekProvision3.isChecked())
        {
            Toast.makeText(getBaseContext(), "개인정보 취급 방침에 동의해주십시요.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!chekProvision4.isChecked())
        {
            Toast.makeText(getBaseContext(), "위치기반서비스 이용약관에 동의해주십시요.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!chekProvision5.isChecked())
        {
            Toast.makeText(getBaseContext(), "고유식별정보 수집에 동의해주십시요.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!mOnlyProvision) {
            Intent intent = new Intent(ProvisionsActivity.this, CertificationActivity.class);
            intent.putExtra("title", "휴대폰 인증");
            intent.putExtra("registry", mRegistry);
            intent.putExtra("idpw_search", mIDPWSearch);
            startActivity(intent);
        } else {
            ConfigPreference configPreference = ConfigPreference.getInstance();
            configPreference.setPreference(getBaseContext());
            configPreference.setUserReagreement(true);

            Intent intent = new Intent(ProvisionsActivity.this, LoginActivity.class);
            startActivity(intent);

            finish();
        }
    }
}
