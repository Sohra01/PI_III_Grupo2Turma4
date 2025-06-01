package com.edu.puc.pi_iii_superid.ui.theme.screens

// Imports necessários
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrEditCategoryScreen(
    navController: NavHostController,
    isEdit: Boolean = true,                 // Indica se é uma edição ou criação de categoria
    categoriaId: String? = null,            // ID da categoria (usado em edição)
    nomeCategoriaInicial: String = ""       // Nome inicial, se vier da edição
) {
    // Instâncias do Firebase
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    // Estados de controle da UI
    var nomeCategoria by remember { mutableStateOf(nomeCategoriaInicial) } // Campo de texto do nome da categoria
    var showDialog by remember { mutableStateOf(false) } // Controle da exibição do AlertDialog de exclusão
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Função para editar a categoria no Firestore
    fun editarCategoria() {
        if (nomeCategoria.trim().isBlank()) {
            Toast.makeText(context, "Digite um nome para a categoria", Toast.LENGTH_SHORT).show()
            return
        }

        val user = auth.currentUser
        if (user != null && categoriaId != null) {
            val dadosAtualizados = mapOf("nome" to nomeCategoria.trim())
            firestore.collection("usuarios")
                .document(user.uid)
                .collection("categorias")
                .document(categoriaId)
                .update(dadosAtualizados)
                .addOnSuccessListener {
                    Toast.makeText(context, "Categoria atualizada!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Erro ao atualizar: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    // Drawer lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onClose = { scope.launch { drawerState.close() } },
                navController
            )
        }
    ) {
        // Scaffold com TopAppBar
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Categorias", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF004A8F))
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
                // Botão de voltar e título
                Row {
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004A8F)),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEdit) "EDITE SUA CATEGORIA" else "ADICIONE UMA CATEGORIA",
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Campo de texto para nome da categoria
                OutlinedTextField(
                    value = nomeCategoria,
                    onValueChange = {
                        if (it.length <= 25) nomeCategoria = it
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.weight(1f)) // Empurra os botões para a parte de baixo

                // Botão de salvar
                Button(
                    onClick = {
                        if (nomeCategoria.trim().isBlank()) {
                            Toast.makeText(context, "Digite um nome para a categoria", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val user = auth.currentUser
                        if (isEdit) {
                            editarCategoria()
                        } else {
                            // Criar nova categoria
                            if (user != null) {
                                val categoria = hashMapOf("nome" to nomeCategoria.trim())
                                firestore.collection("usuarios")
                                    .document(user.uid)
                                    .collection("categorias")
                                    .add(categoria)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Categoria salva com sucesso!", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Erro ao salvar: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                            } else {
                                Toast.makeText(context, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004A8F)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(48.dp)
                ) {
                    Text("SALVAR", color = Color.White)
                }

                // Botão de excluir, exibido apenas no modo de edição
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

                // Diálogo de confirmação de exclusão
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Confirmar exclusão") },
                        text = { Text("Tem certeza que deseja excluir esta categoria?\n(TODAS AS SENHAS DESTA CATEGORIA SERÃO DELETADAS)") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val user = auth.currentUser
                                    if (user != null && categoriaId != null) {
                                        firestore.collection("usuarios")
                                            .document(user.uid)
                                            .collection("categorias")
                                            .document(categoriaId)
                                            .delete()
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Categoria excluída!", Toast.LENGTH_SHORT).show()
                                                navController.popBackStack()
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(context, "Erro ao excluir: ${e.message}", Toast.LENGTH_LONG).show()
                                            }
                                    }
                                }
                            ) {
                                Text("Sim", color = Color.Red)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    }
}
