    package com.edu.puc.pi_iii_superid.ui.theme.screens
    
    import PreferencesManager
    import androidx.compose.foundation.background
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material3.*
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.getValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.text.style.TextAlign
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.runtime.rememberCoroutineScope
    import androidx.compose.runtime.setValue
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.unit.sp
    import androidx.navigation.NavController
    import kotlinx.coroutines.launch

    @Composable
fun TermsOfUseScreen(
    navController: NavController, // Controlador de navegação
    preferencesManager: PreferencesManager // Gerenciador de preferências para salvar se os termos foram aceitos
) {
    val scope = rememberCoroutineScope() // CoroutineScope para executar ações assíncronas

    var isChecked by remember { mutableStateOf(false) } // Estado do checkbox (se está marcado ou não)
    val context = LocalContext.current // Contexto atual da aplicação

    // Layout principal usando Box para centralizar conteúdo
    Box(
        modifier = Modifier
            .fillMaxSize() // Preenche toda a tela
            .background(Color(0xFF0077D7)), // Cor de fundo azul da tela
        contentAlignment = Alignment.Center // Alinha conteúdo no centro
    ) {
        // Coluna que organiza elementos na vertical
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, // Alinha horizontalmente no centro
            modifier = Modifier
                .padding(24.dp) // Espaçamento externo
        ) {
            // Cartão branco que contém os textos dos termos
            Card(
                shape = RoundedCornerShape(24.dp), // Arredondamento das bordas
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp), // Sombra do card
                colors = CardDefaults.cardColors(containerColor = Color.White), // Cor do card (branco)
                modifier = Modifier.padding(bottom = 24.dp) // Espaçamento abaixo do card
            ) {
                // Coluna interna do card
                Column(
                    modifier = Modifier
                        .padding(24.dp) // Espaçamento interno do card
                        .widthIn(max = 320.dp), // Define uma largura máxima
                    horizontalAlignment = Alignment.CenterHorizontally // Centraliza conteúdo horizontalmente
                ) {
                    // Título "TERMOS DE USO"
                    Text(
                        text = "TERMOS DE USO",
                        fontSize = 18.sp, // Tamanho da fonte
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFF1E88E5), // Cor azul no título
                            fontWeight = FontWeight.Bold // Negrito
                        ),
                        modifier = Modifier.padding(bottom = 16.dp) // Espaço abaixo do título
                    )

                    // Texto com o conteúdo dos termos
                    val termsText = """
                        ACEITAÇÃO DOS TERMOS
                        AO ACESSAR E UTILIZAR ESTE APLICATIVO, VOCÊ CONCORDA COM OS PRESENTES TERMOS DE USO.

                        USO PERMITIDO
                        VOCÊ SE COMPROMETE A UTILIZAR ESTE SITE APENAS PARA FINS LEGAIS E DE ACORDO COM ESTES TERMOS.

                        PROPRIEDADE INTELECTUAL
                        TODO O CONTEÚDO DESTE SITE (TEXTOS, IMAGENS, LOGOS, ETC.) É PROTEGIDO POR DIREITOS AUTORAIS E NÃO PODE SER COPIADO OU REPRODUZIDO SEM AUTORIZAÇÃO.

                        MODIFICAÇÕES
                        PODEMOS ALTERAR ESTES TERMOS DE USO A QUALQUER MOMENTO, SENDO RECOMENDÁVEL QUE VOCÊ OS REVISE PERIODICAMENTE.

                        LIMITAÇÃO DE RESPONSABILIDADE
                        NÃO NOS RESPONSABILIZAMOS POR EVENTUAIS DANOS CAUSADOS PELO USO DESTE SITE OU POR INDISPONIBILIDADE TEMPORÁRIA DOS SERVIÇOS.
                    """.trimIndent()

                    // Texto exibindo os termos dentro do card
                    Text(
                        text = termsText,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Black), // Estilo do texto
                        fontSize = 15.sp, // Tamanho do texto
                        textAlign = TextAlign.Center // Centraliza o texto
                    )
                }
            }

            // Linha com o checkbox e texto "Aceitar termos de uso"
            Row(
                verticalAlignment = Alignment.CenterVertically, // Alinha verticalmente no centro
                modifier = Modifier.padding(bottom = 24.dp) // Espaço abaixo da linha
            ) {
                // Checkbox que define se o usuário aceitou os termos
                Checkbox(
                    checked = isChecked, // Estado do checkbox
                    onCheckedChange = { isChecked = it }, // Atualiza o estado quando clicar
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.White, // Cor quando marcado (fundo do box)
                        checkmarkColor = Color(0xFF1E88E5), // Cor do check (✓)
                        uncheckedColor = Color.White // Cor quando não está marcado
                    )
                )
                // Texto ao lado do checkbox
                Text(
                    text = "Aceitar termos de uso",
                    color = Color.White, // Cor branca
                    style = MaterialTheme.typography.bodySmall // Estilo do texto
                )
            }

            // Botão "ACEITAR"
            Button(
                onClick = {
                    scope.launch { // Lança uma coroutine
                        preferencesManager.setTermsAccepted(true) // Salva nas preferências que os termos foram aceitos
                        navController.navigate("welcome") { // Navega para a tela de boas-vindas
                            popUpTo("termsofuse") { inclusive = true } // Remove a tela de termos da pilha de navegação
                        }
                    }
                },
                enabled = isChecked, // Só habilita o botão se o checkbox estiver marcado
                modifier = Modifier.fillMaxWidth(0.6f), // O botão ocupa 60% da largura
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03A9F4)) // Cor azul clara do botão
            ) {
                // Texto do botão
                Text("ACEITAR", color = Color.White) // Texto branco
            }
        }
    }
}
