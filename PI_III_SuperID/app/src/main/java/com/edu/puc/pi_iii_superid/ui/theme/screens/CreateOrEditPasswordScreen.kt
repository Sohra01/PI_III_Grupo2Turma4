package com.edu.puc.pi_iii_superid.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrEditPasswordScreen( navController: NavHostController,isEdit: Boolean = true,) {
    val senhaVisivel = remember { mutableStateOf(false) }

    val login = remember { mutableStateOf("") }
    val senha = remember { mutableStateOf("") }
    val descricao = remember { mutableStateOf("") }

    val categorias = listOf("Site", "App", "Banco") // Exemplo
    val categoriaSelecionada = remember { mutableStateOf<String?>(null) }
    val expanded = remember { mutableStateOf(false) }

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
                    title = { Text("Suas Senhas", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {drawerState.open()}
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
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

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
                    Text("EDITE / ADICIONE SENHAS!", color = Color.White)
                }

                OutlinedTextField(
                    value = login.value,
                    onValueChange = { login.value = it },
                    label = { Text("Login") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = senha.value,
                    onValueChange = { senha.value = it },
                    label = { Text("Senha*") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { senhaVisivel.value = !senhaVisivel.value }) {
                            Icon(
                                imageVector = if (senhaVisivel.value) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (senhaVisivel.value) VisualTransformation.None else PasswordVisualTransformation()
                )

                // Dropdown de categorias
                ExposedDropdownMenuBox(
                    expanded = expanded.value,
                    onExpandedChange = { expanded.value = !expanded.value }
                ) {
                    OutlinedTextField(
                        value = categoriaSelecionada.value ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categorias*") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }
                    ) {
                        categorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria) },
                                onClick = {
                                    categoriaSelecionada.value = categoria
                                    expanded.value = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = descricao.value,
                    onValueChange = { descricao.value = it },
                    label = { Text("Descrição") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5
                )

                // Botões de ação
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { /* Salvar ação */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004A8F)),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("SALVAR", color = Color.White)
                    }
                    if (isEdit) {
                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { showDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000)),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("EXCLUIR", color = Color.White)
                        }
                    }
                }
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Confirmar exclusão") },
                        text = { Text("Tem certeza que deseja excluir esta senha?") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showDialog = false
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

