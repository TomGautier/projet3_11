using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net.Http;
using Newtonsoft.Json;

namespace PolyPaint.Utilitaires
{
    public class NetworkManager
    {
        private static readonly HttpClient client = new HttpClient();
        private readonly string ipAddress = "127.0.0.1";

        public NetworkManager() { }

        public async Task<string> LoginAsync(string username, string password)
        {
            var response = await client.PostAsync("http://" + ipAddress + ":3000/connection/login/" + username + "/" + password, null);

            return await response.Content.ReadAsStringAsync();
        }

        public async Task<string> SignupAsync(string username, string password)
        {
            var response = await client.PostAsync("http://" + ipAddress + ":3000/connection/signup/" + username + "/" + password, null);

            return await response.Content.ReadAsStringAsync();
        }

        public async Task RequestPwdAsync(string username, string email)
        {
            var response = await client.PostAsync("http://" + ipAddress + ":3000/connection/forgot/" + username + "/" + email, null);
        }

        public async Task ForgotPWDAsync(string username, string oldPassword, string newPassword)
        {
            var response = await client.PostAsync("http://" + ipAddress + ":3000/connection/reset/" + username + "/" + oldPassword + "/" + newPassword, null);
        }

        public async Task<string> LoadUsersAsync(string username, string sessionId)
        {
            var response = await client.GetAsync("http://" + ipAddress + ":3000/api/user/" + sessionId + "/" + username);

            return await response.Content.ReadAsStringAsync();
        }

        public void PostImage(string username, string sessionId, string imageId, string visibility, string protection)
        {
            var bodyTemplate = new
            {
                id = imageId,
                visibility = visibility,
                protection = protection,
                author = username
            };
            var body = new StringContent(JsonConvert.SerializeObject(bodyTemplate), Encoding.UTF8, "application/json");
            var response = client.PostAsync("http://" + ipAddress + ":3000/api/images/" + sessionId + "/" + username, body);
        }

        public async Task<string> LoadAllImageAsync(string username, string sessionId)
        {
            var response = await client.GetAsync("http://" + ipAddress + ":3000/api/images/common/" + sessionId + "/" + username);

            return await response.Content.ReadAsStringAsync();
        }

        public async Task<string> LoadUserImageAsync(string username, string sessionId)
        {
            var response = await client.GetAsync("http://" + ipAddress + ":3000/api/images/" + sessionId + "/" + username);

            return await response.Content.ReadAsStringAsync();
        }
    }
}
