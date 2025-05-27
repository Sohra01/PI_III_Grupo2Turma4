package com.example.superid.ui.theme.screens


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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.edu.puc.pi_iii_superid.data.startQrCodeScanner
import com.edu.puc.pi_iii_superid.ui.theme.screens.DrawerContent
import com.example.superid.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(navController: NavController) {

    data class Categoria(
        val id: String,
        val nome: String
    )

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }



    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    LaunchedEffect(Unit) {
        val user = auth.currentUser
        if (user != null) {
            try {
                val snapshot = firestore.collection("usuarios")
                    .document(user.uid)
                    .collection("categorias")
                    .get()
                    .await()

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
            topBar = {
                TopAppBar(
                    title = { Text("Categorias", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                scope.launch { drawerState.open() }
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
            bottomBar = {
                // Cria o BottomAppBar com espaço para o botão central
                BottomAppBar(
                    modifier = Modifier.height(56.dp),
                    containerColor = Color(0xFF00A6FF),
                    tonalElevation = 5.dp,
                    content = {
                        // Espaço vazio, pois o botão será sobreposto
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { startQrCodeScanner(context) },
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
            },
            floatingActionButtonPosition = FabPosition.Center,
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                    if (loading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (errorMessage != null) {
                        LaunchedEffect(errorMessage) {
                            errorMessage?.let {
                                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {

                        // Conteúdo principal (Grid)
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

                                        if (categoria.nome.lowercase() != "site web") {
                                            IconButton(
                                                onClick = {
                                                    navController.navigate("CreateOrEditCategoryScreen?isEdit=true&categoriaId=${categoria.id}&nome=${categoria.nome}")
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

                FloatingActionButton(
                    onClick = { navController.navigate("CreateOrEditCategoryScreen?isEdit=false") },
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
