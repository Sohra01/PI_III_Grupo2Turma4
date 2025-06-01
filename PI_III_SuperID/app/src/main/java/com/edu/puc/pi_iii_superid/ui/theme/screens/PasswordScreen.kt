    package com.edu.puc.pi_iii_superid.ui.theme.screens


    import android.widget.Toast
    import androidx.compose.foundation.background
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.lazy.LazyColumn
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
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.setValue
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.unit.sp
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.FirebaseFirestore
    import kotlinx.coroutines.tasks.await


    data class Senha(
        val id: String,
        val nome: String,
        val login: String,
        val senha: String,
        val descricao: String,
        val token: String,
        val categoria: String
    )


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PasswordScreen(navController: NavController, categoriaNome: String, categoria: String) {

        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val context = LocalContext.current
        var emailVerificado by remember { mutableStateOf<Boolean?>(null) }

        var senhas by remember { mutableStateOf<List<Senha>>(emptyList()) }
        var loading by remember { mutableStateOf(true) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        val user = auth.currentUser

        LaunchedEffect(categoria, user) {
            if (user != null) {
                emailVerificado = user.isEmailVerified
                loading = true
                try {
                    val snapshot = firestore.collection("usuarios")
                        .document(user.uid)
                        .collection("categorias")
                        .document(categoria)
                        .collection("senhas")
                        .get()
                        .await()

                    senhas = snapshot.documents.map { doc ->
                        val senhaCriptografada = doc.getString("senha") ?: ""
                        val senhaDescriptografada = try {
                            CryptoUtils.decrypt(senhaCriptografada)
                        } catch (e: Exception) {
                            "" // caso ocorra erro na descriptografia
                        }
                        Senha(
                            id = doc.id,
                            nome = doc.getString("nome") ?: "Sem nome",
                            login = doc.getString("login") ?: "",
                            senha = senhaDescriptografada,
                            descricao = doc.getString("descricao") ?: "",
                            token = doc.getString("token") ?: "",
                            categoria = doc.getString("categoria") ?: ""
                        )
                    }
                    loading = false
                } catch (e: Exception) {
                    errorMessage = "Erro ao carregar senhas: ${e.message}"
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
                    if (emailVerificado == true) {
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
                    }else if (emailVerificado == null) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }else {
                        Text(
                            text = "Verifique seu e-mail",
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier.offset(y = 45.dp)
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
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else if (errorMessage != null) {
                        LaunchedEffect(errorMessage) {
                            errorMessage?.let {
                                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
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

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = { /* abrir site */ },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A6FF)),
                                        modifier = Modifier.padding(horizontal = 8.dp),
                                        shape = RoundedCornerShape(20)
                                    ) {
                                        Text(categoriaNome.substringBefore(" "), color = Color.White)
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }

                            items(senhas) { senha ->
                                PasswordCard(
                                    nome = senha.nome,
                                    senha = senha.senha,
                                    onClick = {
                                        navController.navigate(
                                            "CreateOrEditPasswordScreen?isEdit=true&categoria=$categoria&categoriaNome=$categoriaNome&senhaId=${senha.id}"
                                        )
                                    }
                                )
                            }
                        }
                    }

                    FloatingActionButton(
                        onClick = {
                            navController.navigate("CreateOrEditPasswordScreen?isEdit=false&categoria=$categoria&categoriaNome=$categoriaNome")
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



    @Composable
        fun PasswordCard(nome: String, senha: String, onClick: () -> Unit) {
            val senhaVisivel = remember { mutableStateOf(false) }

            Card(
                onClick = onClick,
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

                    IconButton(
                        onClick = { senhaVisivel.value = !senhaVisivel.value },
                        modifier = Modifier.padding(8.dp)
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




