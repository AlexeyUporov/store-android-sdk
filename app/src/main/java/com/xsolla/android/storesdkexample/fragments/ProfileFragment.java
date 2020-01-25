package com.xsolla.android.storesdkexample.fragments;

import android.view.View;
import android.widget.TextView;

import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.xsolla_login_sdk.XLogin;
import com.xsolla.android.xsolla_login_sdk.jwt.JWT;

public class ProfileFragment extends BaseFragment {

    @Override
    int getLayout() {
        return R.layout.fragment_profile;
    }

    @Override
    void initUI() {
        TextView username = rootView.findViewById(R.id.username);
        TextView email = rootView.findViewById(R.id.email);
        TextView tokenType = rootView.findViewById(R.id.token_type);
        TextView publisherId = rootView.findViewById(R.id.publisher_id);
        TextView issuedAt = rootView.findViewById(R.id.issued_at);
        TextView expiresAt = rootView.findViewById(R.id.expires_at);
        TextView issuer = rootView.findViewById(R.id.issuer);

        XLogin xLogin = XLogin.getInstance();
        JWT jwt = xLogin.getJwt();

        username.setText(jwt.getClaim("username").asString());
        email.setText(jwt.getClaim("email").asString());
        tokenType.setText(jwt.getClaim("type").asString());
        publisherId.setText(jwt.getClaim("publisher_id").asString());
        issuedAt.setText(jwt.getIssuedAt().toString());
        expiresAt.setText(jwt.getExpiresAt().toString());
        issuer.setText(jwt.getIssuer());

        rootView.findViewById(R.id.logout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XLogin.getInstance().logout();
                openRootFragment();
            }
        });
    }

}
