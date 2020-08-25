package io.github.lordraydenmk.superheroesapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import io.github.lordraydenmk.superheroesapp.superheroes.SuperheroesFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.container, SuperheroesFragment())
            }
        }
    }
}