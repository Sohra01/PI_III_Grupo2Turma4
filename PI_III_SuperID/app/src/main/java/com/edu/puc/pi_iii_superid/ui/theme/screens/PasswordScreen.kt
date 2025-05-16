package com.edu.puc.pi_iii_superid.ui.theme.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items


data class SenhaSalva(val nome: String, val valor: String)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordScreen(navController: NavController, categoria: String) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Lista de senhas de exemplo
    val senhas = listOf(
        SenhaSalva("Senha 1", "abc123"),
        SenhaSalva("Senha 2", "minhaSenhaSegura"),
        SenhaSalva("Senha 3", "superSecret!@#"),
        SenhaSalva("Senha 4", "superSecret!@#"),
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(onClose = { scope.launch { drawerState.close() } })
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Suas senhas", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color(
                            0xFF004A8F
                        )
                    )
                )
            },
            bottomBar = {
                BottomAppBar(
                    modifier = Modifier.height(56.dp),
                    containerColor = Color(0xFF00A6FF),
                    tonalElevation = 5.dp,
                    content = {}
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { /* Ação central */ },
                    containerColor = Color(0xFF004A8F),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .offset(y = 55.dp)
                        .size(65.dp)
                ) {
                    Icon(
                        Icons.Default.Fullscreen,
                        contentDescription = "Central Action",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.Center,
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(16.dp) // Adiciona espaçamento entre os itens
                ) {
                    item {
                        // Botão voltar
                        Button(
                            onClick = { navController.popBackStack() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004A8F)),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("INTERAJA COM SUAS SENHAS", color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Usando 'items' para gerar dinamicamente os cards
                    items(senhas) { senhaItem ->
                        PasswordCard(nome = senhaItem.nome, senha = senhaItem.valor, categoria = categoria, navController = navController)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                }
                Box(modifier = Modifier.fillMaxSize()) {
                    FloatingActionButton(
                        onClick = { navController.navigate("CreateOrEditPasswordScreen?isEdit=false") },
                        containerColor = Color(0xFF004A8F),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .size(45.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Adicionar",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun PasswordCard(nome: String, senha: String, categoria: String, navController: NavController ) {
    val senhaVisivel = remember { mutableStateOf(false) }

    Card(
        onClick = {navController.navigate("CreateOrEditPasswordScreen")},
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = nome, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = if (senhaVisivel.value) senha else "********",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier
                        .background(Color(0xFFEFEFEF), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Button(
                onClick = { /* abrir site */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(categoria.substringBefore(" "), color = Color.White)
            }

            IconButton(
                onClick = { senhaVisivel.value = !senhaVisivel.value },
                modifier = Modifier
                    .background(Color(0xFFE6F0FA), shape = CircleShape)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = if (senhaVisivel.value) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (senhaVisivel.value) "Ocultar senha" else "Ver senha",
                    tint = Color.Black
                )
            }
        }
    }
}



