package com.edu.puc.pi_iii_superid.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrEditPasswordScreen(
    navController: NavHostController,
    isEdit: Boolean = true,
    categoria: String,
    categoriaNome : String,
    senhaId: String? = null
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val senhaVisivel = remember { mutableStateOf(false) }

    val nome = remember { mutableStateOf("") }
    val login = remember { mutableStateOf("") }
    val senha = remember { mutableStateOf("") }
    val descricao = remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    // Função para gerar token base64
    fun generateBase64Token(lengthChars: Int = 256): String {
        val bytesLength = (lengthChars * 3) / 4
        val random = java.security.SecureRandom()
        val bytes = ByteArray(bytesLength)
        random.nextBytes(bytes)
        return android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
    }

    // Carregar dados para edição
    LaunchedEffect(senhaId) {
        if (isEdit && senhaId != null) {
            val user = auth.currentUser
            if (user != null) {
                try {
                    val doc = firestore.collection("usuarios")
                        .document(user.uid)
                        .collection("categorias")
                        .document(categoria)
                        .collection("senhas")
                        .document(senhaId)
                        .get()
                        .await()
                    if (doc.exists()) {
                        nome.value = doc.getString("nome") ?: ""
                        login.value = doc.getString("login") ?: ""

                        // Pega a senha criptografada do Firestore
                        val senhaCriptografada = doc.getString("senha") ?: ""

                        // Descriptografa a senha
                        senha.value = if (senhaCriptografada.isNotEmpty()) {
                            try {
                                CryptoUtils.decrypt(senhaCriptografada)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Erro ao descriptografar senha: ${e.message}", Toast.LENGTH_LONG).show()
                                "" // senha vazia em caso de erro
                            }
                        } else {
                            ""
                        }

                        descricao.value = doc.getString("descricao") ?: ""
                        // Categoria já vem do parâmetro
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Erro ao carregar senha: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Função para salvar a senha (criar ou atualizar)
    fun salvarSenha(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        categoriaId: String,
        nome: String,
        login: String,
        senha: String,
        descricao: String,
        isEdit: Boolean,
        senhaId: String? = null,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
        gerarToken: () -> String
    ) {
        val userId = auth.currentUser?.uid ?: return
        val token = gerarToken()

        // Criptografar a senha antes de salvar
        val senhaCriptografada = try {
            CryptoUtils.encrypt(senha)
        } catch (e: Exception) {
            onFailure(e)
            return
        }

        val senhaData = hashMapOf(
            "nome" to nome,
            "login" to login,
            "senha" to senhaCriptografada,  // SALVA A SENHA CRIPTOGRAFADA
            "descricao" to descricao,
            "token" to token
        )

        val senhasRef = firestore.collection("usuarios")
            .document(userId)
            .collection("categorias")
            .document(categoriaId)
            .collection("senhas")

        if (isEdit && senhaId != null) {
            senhasRef.document(senhaId)
                .set(senhaData)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFailure(it) }
        } else {
            senhasRef
                .add(senhaData)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFailure(it) }
        }
    }

    // Função para excluir a senha
    fun excluirSenha(categoriaId: String, senhaId: String) {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(context, "Usuário não autenticado", Toast.LENGTH_LONG).show()
            return
        }

        scope.launch {
            try {
                firestore.collection("usuarios")
                    .document(user.uid)
                    .collection("categorias")
                    .document(categoriaId)
                    .collection("senhas")
                    .document(senhaId)
                    .delete()
                    .await()

                Toast.makeText(context, "Senha excluída com sucesso!", Toast.LENGTH_LONG).show()
                navController.popBackStack()
            } catch (e: Exception) {
                Toast.makeText(context, "Erro ao excluir senha: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onClose = { scope.launch { drawerState.close() } },
                navController = navController
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Suas Senhas", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
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
                    Text(
                        text = if (isEdit) "EDITE SUA SENHA" else "ADICIONE UMA SENHA",
                        color = Color.White
                    )
                }

                OutlinedTextField(
                    value = nome.value,
                    onValueChange = { nome.value = it },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth()
                )

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

                OutlinedTextField(
                    value = categoriaNome,
                    onValueChange = {},
                    label = { Text("Categoria") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = descricao.value,
                    onValueChange = { descricao.value = it },
                    label = { Text("Descrição") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {

                            if (senha.value.isBlank()) {
                                Toast.makeText(context, "A senha não pode estar vazia", Toast.LENGTH_LONG).show()
                                return@Button
                            }

                            salvarSenha(
                                firestore = firestore,
                                auth = auth,
                                categoriaId = categoria,
                                nome = nome.value,
                                login = login.value,
                                senha = senha.value,
                                descricao = descricao.value,
                                isEdit = isEdit,
                                senhaId = senhaId,
                                onSuccess = {
                                    Toast.makeText(context, "Senha salva com sucesso!", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                },
                                onFailure = {
                                    Toast.makeText(context, "Erro ao salvar senha: ${it.message}", Toast.LENGTH_SHORT).show()
                                },
                                gerarToken = {
                                    generateBase64Token()
                                }
                            )
                        },
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
                                    senhaId?.let {
                                        excluirSenha(categoriaId = categoria, senhaId = it)
                                    } ?: Toast.makeText(context, "Erro: ID da senha é nulo", Toast.LENGTH_LONG).show()
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
