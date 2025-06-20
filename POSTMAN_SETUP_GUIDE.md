# 📮 Guia de Configuração - Postman Collection

## 🚀 Como Importar e Usar a Collection

### **1. Importar a Collection no Postman**

1. **Abra o Postman**
2. **Clique em "Import"** (botão no canto superior esquerdo)
3. **Selecione "Upload Files"**
4. **Escolha o arquivo:** `Municipal-Servidor-API.postman_collection.json`
5. **Clique em "Import"**

### **2. Configurar Variáveis (Automático)**
A collection já vem configurada com:
- **baseUrl**: `http://localhost:8080`
- **secretariaId**: será preenchido automaticamente ao criar secretarias
- **servidorId**: será preenchido automaticamente ao criar servidores

### **3. Estrutura da Collection**

#### 🏛️ **Secretarias (8 endpoints)**
- 📋 Listar todas as secretarias
- 🔍 Buscar secretaria por ID
- 🔍 Buscar secretaria por sigla
- 🔍 Buscar secretarias por nome
- ✅ Verificar se secretaria existe
- ➕ Criar nova secretaria
- ✏️ Atualizar secretaria
- 🗑️ Remover secretaria

#### 👥 **Servidores (10 endpoints)**
- 📋 Listar todos os servidores
- 🔍 Buscar servidor por ID
- 🔍 Buscar servidor por email
- 🔍 Buscar servidores por secretaria
- 📊 Contar servidores por secretaria
- 🔍 Buscar servidores por nome
- ✅ Verificar se servidor existe
- ➕ Criar novo servidor
- ✏️ Atualizar servidor
- 🗑️ Remover servidor

#### 🧪 **Cenários de Teste Automatizados**
- Fluxo completo: Criar Secretaria → Criar Servidor → Validar Relacionamento

## 🎯 Começando os Testes

### **Opção 1: Teste Individual**
1. Execute primeiro: **"➕ Criar nova secretaria"**
2. Copie o ID retornado
3. Cole no campo `secretariaId` das variáveis
4. Execute: **"➕ Criar novo servidor"**

### **Opção 2: Cenário Automático (Recomendado)**
1. Acesse: **🧪 Cenários de Teste**
2. Execute: **"📝 Cenário Completo - Criar Secretaria e Servidor"**
3. Os IDs serão automaticamente salvos nas variáveis

## 🔧 Exemplos de Requests

### **Criar Secretaria**
```json
POST {{baseUrl}}/api/secretarias
{
  "nome": "Secretaria de Educação",
  "sigla": "SEDUC"
}
```

### **Criar Servidor**
```json
POST {{baseUrl}}/api/servidores
{
  "nome": "João Silva Santos",
  "email": "joao.silva@prefeitura.gov.br",
  "dataNascimento": "1985-03-15",
  "secretaria": {
    "id": "{{secretariaId}}"
  }
}
```

## ✅ Validações Incluídas

### **Secretaria**
- Nome: 2-100 caracteres, obrigatório
- Sigla: 2-10 caracteres, única, maiúsculo

### **Servidor**
- Nome: 2-100 caracteres, obrigatório
- Email: formato válido, único
- Data Nascimento: idade entre 18-75 anos
- Secretaria: deve existir no sistema

## 🚨 Códigos de Resposta

- **200 OK**: Operação realizada com sucesso
- **201 Created**: Recurso criado com sucesso
- **204 No Content**: Recurso deletado com sucesso
- **400 Bad Request**: Erro de validação
- **404 Not Found**: Recurso não encontrado
- **500 Internal Server Error**: Erro interno

## 🔄 Scripts Automáticos

A collection inclui scripts que:
- **Salvam automaticamente** IDs de recursos criados
- **Executam testes automáticos** de validação
- **Exibem logs** de depuração no console
- **Verificam status codes** esperados

## 🎛️ Variáveis de Ambiente

Se preferir, você pode criar um **Environment** separado:

1. **Clique no ícone de engrenagem** → "Manage Environments"
2. **Adicione um novo environment** chamado "Municipal API"
3. **Configure as variáveis:**
   - `baseUrl`: `http://localhost:8080`
   - `secretariaId`: (vazio inicialmente)
   - `servidorId`: (vazio inicialmente)

## 🚀 Pronto para Testar!

Sua API está rodando em `http://localhost:8080` e a collection está pronta para uso. Comece executando o **"Cenário Completo"** para ver toda a API funcionando! 