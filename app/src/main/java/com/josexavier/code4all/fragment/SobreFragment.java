package com.josexavier.code4all.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.Configs;

public class SobreFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sobre, container, false);

        ImageButton facebook, instagram, github, linkedin;
        facebook = root.findViewById(R.id.buttonFacebookSobre);
        instagram = root.findViewById(R.id.buttonInstagramSobre);
        github = root.findViewById(R.id.buttonGitHubSobre);
        linkedin = root.findViewById(R.id.buttonLinkedInSobre);

        Button enviarEmail;
        enviarEmail = root.findViewById(R.id.buttonEnviarEmailSobre);

        facebook.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/josexavier420"))));
        instagram.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/rednexx46/"))));
        github.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/rednexx46"))));
        linkedin.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/jose-xavier46/"))));
        enviarEmail.setOnClickListener(v -> Configs.recuperarNomeUtilizador(nomeUtilizador -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("message/rfc822");
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Configs.emailCriador});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Mensagem - Code4All");
            intent.putExtra(Intent.EXTRA_TEXT, "Boas,\n\n\n\nCumprimentos,\n\n" + nomeUtilizador);
            startActivity(intent);
        }));

        return root;

    }
}