using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using PolyPaint.Utilitaires;

namespace UnitTestProject1
{
    [TestClass]
    public class UnitTest1
    {
        [TestMethod]
        public void ValidLogin()
        {
            NetworkManager networkManager = new NetworkManager();
            
            Assert.AreNotEqual(networkManager.LoginAsync("Tom","basicpwd").Result, "");
        }

        [TestMethod]
        public void InvalidLogin()
        {
            NetworkManager networkManager = new NetworkManager();

            Assert.AreEqual(networkManager.LoginAsync("invalid","attempt").Result, "");
        }
    }
}
