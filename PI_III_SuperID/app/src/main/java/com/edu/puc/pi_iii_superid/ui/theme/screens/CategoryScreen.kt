package com.example.superid.ui.theme.screens


import androidx.compose.foundation.background
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.edu.puc.pi_iii_superid.ui.theme.screens.DrawerContent
import com.example.superid.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(navController: NavController) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onClose = { scope.launch { drawerState.close() } }
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
                    onClick = { /* Botão central */ },
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
                            .background(Color(0xFF004A8F), RoundedCornerShape(50))
                            .padding(7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("SELECIONE OU CRIE CATEGORIAS!", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            listOf(
                                "Sites Web",
                                "Aplicativo",
                                "Teclados de Acesso Físico"
                            )
                        ) { categoria ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                onClick = {
                                    navController.navigate("passwords/${categoria}")
                                }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = categoria,
                                        modifier = Modifier.align(Alignment.TopStart),
                                        color = Color(0xFF00A6FF),
                                        fontWeight = FontWeight.Bold
                                    )
                                    if(categoria != "Sites Web") {
                                        IconButton(
                                            onClick = { navController.navigate("CreateOrEditCategoryScreen") },
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

                FloatingActionButton(
                    onClick = { navController.navigate("CreateOrEditCategoryScreen?isEdit=false") },
                    containerColor = Color(0xFF004A8F),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .size(45.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar", tint = Color.White)
                }
            }
        }
    }
}
