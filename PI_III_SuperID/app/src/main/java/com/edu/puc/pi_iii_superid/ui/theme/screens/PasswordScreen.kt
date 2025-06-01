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


// Data class que representa uma senha armazenada
data class Senha( 
    val id: String,          // ID único da senha no Firestore
    val nome: String,        // Nome associado à senha (ex: serviço)
    val login: String,       // Login ou usuário relacionado
    val senha: String,       // Senha descriptografada
    val descricao: String,   // Descrição adicional
    val token: String,       // Token relacionado (se houver)
    val categoria: String    // Categoria à qual a senha pertence
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordScreen(navController: NavController, categoriaNome: String, categoria: String) {

    val auth = FirebaseAuth.getInstance() // Instância do FirebaseAuth para autenticação
    val firestore = FirebaseFirestore.getInstance() // Instância do Firestore para banco de dados
    val context = LocalContext.current // Contexto atual para exibir Toasts e outros recursos
    var emailVerificado by remember { mutableStateOf<Boolean?>(null) } // Estado para saber se email do usuário está verificado

    var senhas by remember { mutableStateOf<List<Senha>>(emptyList()) } // Lista de senhas carregadas
    var loading by remember { mutableStateOf(true) } // Estado que indica se está carregando dados
    var errorMessage by remember { mutableStateOf<String?>(null) } // Mensagem de erro caso ocorra algum problema

    val drawerState = rememberDrawerState(DrawerValue.Closed) // Estado para o menu lateral (drawer)
    val scope = rememberCoroutineScope() // CoroutineScope para lançar coroutines na UI

    val user = auth.currentUser // Usuário autenticado atual

    // Efeito que executa quando 'categoria' ou 'user' mudam para carregar senhas da categoria no Firestore
    LaunchedEffect(categoria, user) {
        if (user != null) {
            emailVerificado = user.isEmailVerified // Verifica se email está verificado
            loading = true // Indica que começou a carregar
            try {
                // Consulta para pegar todas as senhas dentro da categoria do usuário
                val snapshot = firestore.collection("usuarios")
                    .document(user.uid)
                    .collection("categorias")
                    .document(categoria)
                    .collection("senhas")
                    .get()
                    .await() // Espera o resultado da consulta

                // Mapeia os documentos para objetos Senha, descriptografando a senha
                senhas = snapshot.documents.map { doc ->
                    val senhaCriptografada = doc.getString("senha") ?: ""
                    val senhaDescriptografada = try {
                        CryptoUtils.decrypt(senhaCriptografada) // Descriptografa a senha
                    } catch (e: Exception) {
                        "" // Se erro, retorna string vazia para senha
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
                loading = false // Termina o carregamento
            } catch (e: Exception) {
                errorMessage = "Erro ao carregar senhas: ${e.message}" // Armazena mensagem de erro
                loading = false
            }
        } else {
            errorMessage = "Usuário não autenticado" // Se usuário não estiver logado
            loading = false
        }
    }

    // Layout principal com menu lateral (drawer)
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
                        containerColor = Color(0xFF004A8F) // Cor azul escura do topo
                    )
                )
            },
            bottomBar = {
                BottomAppBar(
                    modifier = Modifier.height(56.dp),
                    containerColor = Color(0xFF00A6FF), // Cor azul clara do rodapé
                    tonalElevation = 5.dp,
                    content = {}
                )
            },
            floatingActionButton = {
                // Mostra o FAB apenas se o email do usuário estiver verificado
                if (emailVerificado == true) {
                    FloatingActionButton(
                        onClick = { navController.navigate("camera") }, // Navega para tela da câmera
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
                } else if (emailVerificado == null) {
                    // Enquanto o estado de verificação do email está indefinido, mostra progress indicator
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Se email não está verificado, mostra mensagem pedindo para verificar
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

                // Mostra indicador de carregamento se estiver carregando
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (errorMessage != null) {
                    // Mostra um toast com a mensagem de erro quando ela é setada
                    LaunchedEffect(errorMessage) {
                        errorMessage?.let {
                            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    // Lista de senhas em LazyColumn com espaçamento entre itens
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        // Primeiro item com botões e título
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Botão para voltar na navegação
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

                                // Botão mostrando o nome da categoria, cortando após o primeiro espaço
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

                        // Itens da lista de senhas, exibindo um cartão para cada senha
                        items(senhas) { senha ->
                            PasswordCard(
                                nome = senha.nome,
                                senha = senha.senha,
                                onClick = {
                                    // Navega para tela de edição/criação passando parâmetros pela rota
                                    navController.navigate(
                                        "CreateOrEditPasswordScreen?isEdit=true&categoria=$categoria&categoriaNome=$categoriaNome&senhaId=${senha.id}"
                                    )
                                }
                            )
                        }
                    }
                }

                // Botão flutuante para adicionar nova senha, sempre visível no canto inferior direito
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
    val senhaVisivel = remember { mutableStateOf(false) } // Estado que controla se a senha está visível ou oculta

    Card(
        onClick = onClick, // Ação ao clicar no cartão
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = nome, style = MaterialTheme.typography.bodyLarge) // Nome da senha (serviço)
                Text(
                    text = if (senhaVisivel.value) senha else "********", // Exibe senha ou oculta com asteriscos
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier
                        .background(Color(0xFFEFEFEF), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            IconButton(
                onClick = { senhaVisivel.value = !senhaVisivel.value }, // Alterna visibilidade da senha
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
