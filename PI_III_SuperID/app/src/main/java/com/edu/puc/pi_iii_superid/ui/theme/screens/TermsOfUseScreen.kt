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
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.text.style.TextAlign
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.runtime.rememberCoroutineScope
    import androidx.compose.runtime.setValue
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.unit.sp
    import androidx.navigation.NavController
    import kotlinx.coroutines.CoroutineScope
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.launch

    @Composable
    fun TermsOfUseScreen(
        navController: NavController,
        preferencesManager: PreferencesManager
    ) {
        val scope = rememberCoroutineScope()

        var isChecked by remember { mutableStateOf(false) }
        val context = LocalContext.current
    
    
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0077D7)), // Cor de fundo azul da tela
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(24.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E88E5)), // azul mais claro
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .widthIn(max = 320.dp), // largura máxima
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TERMOS DE USO",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
    
                        val termsText = """
                            ACEITAÇÃO DOS TERMOS
                            AO ACESSAR E UTILIZAR ESTE SITE, VOCÊ CONCORDA COM OS PRESENTES TERMOS DE USO.
    
                            USO PERMITIDO
                            VOCÊ SE COMPROMETE A UTILIZAR ESTE SITE APENAS PARA FINS LEGAIS E DE ACORDO COM ESTES TERMOS.
    
                            PROPRIEDADE INTELECTUAL
                            TODO O CONTEÚDO DESTE SITE (TEXTOS, IMAGENS, LOGOS, ETC.) É PROTEGIDO POR DIREITOS AUTORAIS E NÃO PODE SER COPIADO OU REPRODUZIDO SEM AUTORIZAÇÃO.
    
                            MODIFICAÇÕES
                            PODEMOS ALTERAR ESTES TERMOS DE USO A QUALQUER MOMENTO, SENDO RECOMENDÁVEL QUE VOCÊ OS REVISE PERIODICAMENTE.
    
                            LIMITAÇÃO DE RESPONSABILIDADE
                            NÃO NOS RESPONSABILIZAMOS POR EVENTUAIS DANOS CAUSADOS PELO USO DESTE SITE OU POR INDISPONIBILIDADE TEMPORÁRIA DOS SERVIÇOS.
                        """.trimIndent()
    
                        Text(
                            text = termsText,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                            fontSize = 9.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
    
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { isChecked = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color.White,
                            checkmarkColor = Color(0xFF1E88E5)
                        )
                    )
                    Text(
                        text = "Aceitar termos de uso",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = {
                        scope.launch {
                            preferencesManager.setTermsAccepted(true)
                            navController.navigate("welcome") {
                                popUpTo("termsofuse") { inclusive = true }
                            }
                        }
                    },
                    enabled = isChecked,
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Text("ACEITAR")
                }

            }
        }
    }
