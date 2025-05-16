package com.edu.puc.pi_iii_superid.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrEditCategoryScreen(
    navController: NavHostController,
    isEdit: Boolean = true,
    onSalvarClick: () -> Unit = {},
    onExcluirClick: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }
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
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Botão voltar
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004A8F)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("EDITE / ADICIONE CATEGORIAS!", color = Color.White)
                }

                Spacer(modifier = Modifier.height(32.dp))

                var nomeCategoria by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = nomeCategoria,
                    onValueChange = {
                        if (it.length <= 25) {
                            nomeCategoria = it
                        }
                    },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Spacer flexível para empurrar os botões para o fim da tela
                Spacer(modifier = Modifier.weight(1f))

                // Botão salvar
                Button(
                    onClick = onSalvarClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004A8F)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(48.dp)
                ) {
                    Text("SALVAR", color = Color.White)
                }

                if (isEdit) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000)),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(48.dp)
                    ) {
                        Text("EXCLUIR", color = Color.White)
                    }
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Confirmar exclusão") },
                        text = { Text("Tem certeza que deseja excluir esta categoria?") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showDialog = false
                                    onExcluirClick()
                                }
                            ) {
                                Text("Sim", color = Color.Red)
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showDialog = false }
                            ) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    }
}
