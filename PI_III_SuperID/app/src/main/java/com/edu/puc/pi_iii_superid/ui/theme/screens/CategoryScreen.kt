package com.example.superid.ui.theme.screens

// Imports necessários
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.edu.puc.pi_iii_superid.ui.theme.screens.DrawerContent
import com.example.superid.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(navController: NavController) {

    // Classe local representando uma categoria
    data class Categoria(
        val id: String,
        val nome: String
    )

    // Instâncias do Firebase Auth e Firestore
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    var emailVerificado by remember { mutableStateOf<Boolean?>(null) } // Verificação de e-mail do usuário

    // Estados da tela
    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope() // Corrotina para abrir/fechar o drawer
    val drawerState = rememberDrawerState(DrawerValue.Closed) // Estado do menu lateral (drawer)

    // Efeito que busca as categorias do Firestore assim que a tela for exibida
    LaunchedEffect(Unit) {
        val user = auth.currentUser
        if (user != null) {
            emailVerificado = user.isEmailVerified
            try {
                val snapshot = firestore.collection("usuarios")
                    .document(user.uid)
                    .collection("categorias")
                    .get()
                    .await()

                // Mapeia os documentos do Firestore para objetos Categoria
                categorias = snapshot.documents.mapNotNull {
                    val nome = it.getString("nome")
                    val id = it.id
                    if (nome != null) Categoria(id, nome) else null
                }

                loading = false
            } catch (e: Exception) {
                errorMessage = "Erro ao carregar categorias: ${e.message}"
                loading = false
            }
        } else {
            errorMessage = "Usuário não autenticado"
            loading = false
        }
    }

    // Drawer com menu lateral personalizado
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onClose = { scope.launch { drawerState.close() } },
                navController
            )
        }
    ) {
        Scaffold(
            // TopBar com botão de menu
            topBar = {
                TopAppBar(
                    title = { Text("Categorias", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color(0xFF004A8F)
                    )
                )
            },
            // BottomAppBar decorativa
            bottomBar = {
                BottomAppBar(
                    modifier = Modifier.height(56.dp),
                    containerColor = Color(0xFF00A6FF),
                    tonalElevation = 5.dp,
                    content = { /* Espaço vazio, apenas visual */ }
                )
            },
            // Botão flutuante central
            floatingActionButton = {
                when (emailVerificado) {
                    true -> {
                        FloatingActionButton(
                            onClick = { navController.navigate("camera") },
                            containerColor = Color(0xFF004A8F),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .offset(y = 45.dp)
                                .size(75.dp)
                        ) {
                            Icon(
                                Icons.Default.Fullscreen,
                                contentDescription = "Central Action",
                                tint = Color.White,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }
                    null -> {
                        // Indicador de carregamento enquanto verifica e-mail
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    false -> {
                        // Mensagem informando que o e-mail não foi verificado
                        Text(
                            text = "Verifique seu e-mail",
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier.offset(y = 45.dp)
                        )
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.Center,
        ) { paddingValues ->
            // Conteúdo principal da tela
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    loading -> {
                        // Indicador de carregamento
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    errorMessage != null -> {
                        // Exibe um Toast com mensagem de erro
                        LaunchedEffect(errorMessage) {
                            errorMessage?.let {
                                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    else -> {
                        // Coluna com o título e a grade de categorias
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "SELECIONE OU CRIE CATEGORIAS!",
                                    color = Color(0xFF004A8F),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Grade com os cards de categorias
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(8.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(categorias) { categoria ->
                                    Card(
                                        shape = RoundedCornerShape(16.dp),
                                        elevation = CardDefaults.cardElevation(8.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp),
                                        onClick = {
                                            // Navega para a tela de senhas da categoria selecionada
                                            navController.navigate("passwords/${categoria.id}/${categoria.nome}")
                                        }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(8.dp)
                                        ) {
                                            Text(
                                                text = categoria.nome,
                                                modifier = Modifier.align(Alignment.TopStart),
                                                color = Color(0xFF00A6FF),
                                                fontWeight = FontWeight.Bold
                                            )

                                            // Botão de edição (exceto para "site web")
                                            if (categoria.nome.lowercase() != "site web") {
                                                IconButton(
                                                    onClick = {
                                                        navController.navigate(
                                                            "CreateOrEditCategoryScreen?isEdit=true&categoriaId=${categoria.id}&nome=${categoria.nome}"
                                                        )
                                                    },
                                                    modifier = Modifier.align(Alignment.BottomEnd)
                                                ) {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.ic_edit),
                                                        contentDescription = "Editar",
                                                        tint = Color(0xFF00A6FF),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Botão de adicionar nova categoria
                FloatingActionButton(
                    onClick = {
                        navController.navigate("CreateOrEditCategoryScreen?isEdit=false")
                    },
                    containerColor = Color(0xFF004A8F),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(26.dp)
                        .size(55.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar", tint = Color.White)
                }
            }
        }
    }
}
